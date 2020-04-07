package com.mpetri.TikaDocsWarc;

import static java.nio.file.FileVisitOption.*;
import static java.nio.file.FileVisitResult.*;

import java.io.*;
import java.io.FileInputStream;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.*;
import java.util.*;
import java.util.zip.*;
import java.util.ArrayDeque;
import java.util.concurrent.*;
import org.apache.commons.cli.*;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.commons.math3.util.Precision;
import org.apache.logging.log4j.*;
import org.apache.logging.log4j.core.config.*;
import org.apache.tika.exception.TikaException;
import org.apache.tika.language.LanguageIdentifier;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.sax.BodyContentHandler;
import org.archive.io.ArchiveReader;
import org.archive.io.ArchiveRecord;
import org.archive.io.ArchiveRecordHeader;
import org.archive.io.warc.WARCReaderFactory;
import org.xml.sax.SAXException;
import opennlp.tools.langdetect.*;
import opennlp.tools.util.*;

public final class TikaDocsWarc {
  public static final String FIELD_BODY = "contents";
  public static final String FIELD_URL = "url";
  public static final String FIELD_REALURL = "rurl";
  public static final String FIELD_DATE = "date";
  public static final String FIELD_ID = "id";

  public static class detectLangFile implements Callable {
    private Path warcFile;
    private String outDir;
    private HtmlParser parser; 
    private LanguageDetector lang_detector;

    public detectLangFile(Path warcFile, LanguageDetector lang_detector, String outDir) {
      this.warcFile = warcFile;
      this.outDir = outDir;
      this.lang_detector = lang_detector;
      this.parser = new HtmlParser();
    }

    public void safePrintln(String s) {
      synchronized(System.out) {
        System.out.println(s);
      }
    }

    // Given WARC data, creates a pseudo header and adds the WARC-TREC-ID to the header
    final String getWarcHeader(Map<String, Object> headers, String basename, int seq) {

      String header = "WARC/1.0\r\n";
      Iterator it = headers.entrySet().iterator();
      while(it.hasNext()) {
        Map.Entry pair = (Map.Entry)it.next();
        String key = pair.getKey().toString();
        if (key.compareTo("absolute-offset") != 0 && key.compareTo("reader-identifier") != 0) {
          header += pair.getKey() + ": " + pair.getValue() + "\r\n";         
        }
      }
      // Warc file header has no TREC ID, so we construct one
      // 0th sequence is the WARC header, so we skip that one
      if (seq > 0) {
        header += "WARC-TREC-ID: " + basename + "-" + Integer.toString(seq) + "\r\n";
      }

      header += "\r\n";
      return header;
    }

    // Handles the processing of a single WARC file
    public Integer call() {
      final Logger LOG = LogManager.getLogger(TikaDocsWarc.class);
      int docCount = 0; // Counts documents seen in this WARC file

      // (1) open warc file
      FileInputStream is;
      GZIPOutputStream os = null;
      ArchiveReader ar;
      // 'bitvector' of documents to keep or throw away
      ArrayList<Boolean> keep = new ArrayList<Boolean>();
      int rec = 0;

      // (2) Process the WARC file
      try {

        is = new FileInputStream(warcFile.toString());
        // The file name identifies the ArchiveReader and indicates if it should be decompressed
        ar = WARCReaderFactory.get(warcFile.toString(), is, true);

        // Once we have an ArchiveReader, we can work through each of the records it contains
        for (ArchiveRecord r: ar) {
          rec++;

          // The header file contains information such as the type of record, size, creation time, and URL
          ArchiveRecordHeader header = r.getHeader();
          String url = header.getUrl();
          String realUrl = header.getUrl();
          String mimetype = header.getMimetype();
          String date = header.getDate();

          if (!mimetype.contains("application/http")) {
            LOG.error(rec + " SKIP url = " + url + " date = " + date + " mime = " + mimetype);
            // Though we are skipping non http docs, we do want to keep the WARC header
            if(mimetype.contains("application/warc-fields")) {
              keep.add(true);
            } else {
              keep.add(false);
            }
            continue;
          }

          // tika parse content
          try {
            BodyContentHandler handler = new BodyContentHandler(-1);
            Metadata metadata = new Metadata();
            parser.parse(r, handler, metadata);
            String parsedContent = handler.toString();

            // Detect the language from the document
            Language lang = lang_detector.predictLanguage(parsedContent);

            // Only store English documents
            String langStr = lang.getLang();
            if (langStr.compareTo("eng") == 0 ) {
              keep.add(true);
            }
            else {
              keep.add(false);
            }
            docCount++;

            // Catch possible exceptions 
          } catch (TikaException e) {
            safePrintln("TIKA " + url);
            keep.add(false);
            docCount++;
          } catch (IOException e) {
            safePrintln("TIKA " + url);
            keep.add(false);
            docCount++;
          } catch (SAXException e) {
            safePrintln("TIKA " + url);
            keep.add(false);
            docCount++;
          } catch (Exception e) {
            safePrintln("TIKA " + url);
            keep.add(false);
            docCount++;
          } // end try/catch
        } // end for loop
      } catch (FileNotFoundException e) {
        safePrintln("Warc Parser: " + e.toString());
      } catch (IOException e) {
        safePrintln("Warc Parser: " + e.toString());
      }

      // (3) Re-iterate file, and only save the data we want to keep
      int index = 0;
      int saved = 0;
      try {	
        // Now we know which records to keep and which to throw out. Re-iterate and dump appropriately
        safePrintln("Read: " + rec + " documents. List length = " + keep.size());
        String outFileName = warcFile.getFileName().toString().replaceAll(".warc.gz", "_ENG.warc.gz");
        outFileName = outDir + "/" + outFileName;
        String baseName = warcFile.getFileName().toString().replaceAll(".warc.gz", "");
        FileOutputStream myOut = new FileOutputStream(outFileName);
        os = new GZIPOutputStream(myOut);
        PrintStream osPlainText = new PrintStream(os);
        try {

          is = new FileInputStream(warcFile.toString());
          ar = WARCReaderFactory.get(warcFile.toString(), is, true);

          // For each record 
          for (ArchiveRecord r: ar) {
            // Keep? 
            if (keep.get(index)) {
              ArchiveRecordHeader header = r.getHeader();
              Map<String,Object> hdrs = header.getHeaderFields();
              String stringHeader = getWarcHeader(hdrs,baseName,index);
              osPlainText.print(stringHeader);
              r.dump(os);
              osPlainText.print("\r\n"); //\r\n");
              ++saved;
            }
            ++index;
          }
          // Catch various exceptions 
        } catch (FileNotFoundException e) {
          safePrintln("Warc Parser: " + e.toString());
        } catch (IOException e) {
          safePrintln("Warc Parser: " + e.toString());
        }
      } catch (FileNotFoundException e) {
        safePrintln("Output: " + e.toString());
      } catch (IOException e) {
        safePrintln("Output: " + e.toString());
      }
      // Don't forget to close the file handle or we'll break the gzip output
      try {
        if(os != null)
          os.close();
      }
      catch (IOException e) {
        safePrintln("Close: " + e.toString());
      }            
      return saved;
    }

  }

  // Main method
  public static void main(String[] args) throws Exception {
    final Logger LOG = LogManager.getLogger(TikaDocsWarc.class);

    // (0) parse command line options
    Options options = new Options();
    options.addOption("help", false, "print this help output");
    options.addOption("input", true, "input directory with warc files");
    options.addOption("lm", true, "language model path");
    options.addOption("output", true, "output directory for target warc files");
    CommandLineParser parser = new DefaultParser();
    CommandLine cmd;
    try {
      // parse the command line arguments
      cmd = parser.parse(options, args);
    } catch (Exception exp) {
      // oops, something went wrong
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp("TikaLuceneWarc", options);
      System.err.println("Parsing failed.    Reason: " + exp.getMessage());
      return;
    }
    String inputDir = "";
    String outputDir = "";
    String languageModel = "";

    if (cmd.hasOption("input")) {
      inputDir = cmd.getOptionValue("input");
    }
    if (cmd.hasOption("lm")) {
      languageModel = cmd.getOptionValue("lm");
    }
    if (cmd.hasOption("output")) {
      outputDir = cmd.getOptionValue("output");
    }
    if (inputDir == "" || languageModel == "" || outputDir == "") {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp("TikaDocsWarc", options);
      System.err.println("Command line parsing failed. missing input/base/output.");
      return;
    }
    LOG.info("input dir = " + inputDir);
    LOG.info("output dir = " + outputDir);
    LOG.info("lm = " + languageModel);

    // (1) determine input files
    final ArrayDeque < Path > inputFileStack = new ArrayDeque < > ();
    final PathMatcher matcher =
      FileSystems.getDefault().getPathMatcher("glob:" + inputDir + "**/*.warc.gz");
    Files.walkFileTree(
        Paths.get(inputDir),
        new SimpleFileVisitor < Path > () {
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
        throws IOException {
        if (matcher.matches(file)) {
        inputFileStack.add(file);
        }
        return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
        }
        });
    LOG.info("Found " + inputFileStack.size() + " warc files.");

    // (2) configure opennlp stack and run the filtering
    {
      File modelFile = new File(languageModel);
      LanguageDetectorModel trainedModel = new LanguageDetectorModel(modelFile);

      // load the model
      LanguageDetector lang_detector = new LanguageDetectorME(trainedModel);

      // (3) iterate over all files and index
      final long start = System.nanoTime();
      int processors = Runtime.getRuntime().availableProcessors();
      ExecutorService es = Executors.newFixedThreadPool(processors);
      int numFiles = inputFileStack.size();
      LOG.info("threads = " + processors);
      LOG.info("files = " + numFiles);
      final ArrayDeque < Future > outputFutureStack = new ArrayDeque < > ();
      for (Path warcFile: inputFileStack) {
        Future < Integer > future =
          es.submit(new detectLangFile(warcFile, lang_detector, outputDir));
        outputFutureStack.add(future);
      }

      int numProcessed = 0;
      for (Future < Integer > future: outputFutureStack) {
        try {
          Integer processed_docs = future.get();
          numProcessed++;
          double percent = (double) numProcessed / (double) numFiles * 100;
          LOG.info(
              "processed " + numProcessed + "/" + numFiles + " (" + Precision.round(percent, 2) + "%) - " + processed_docs);
        } catch (ExecutionException ex) {
          ex.getCause().printStackTrace();
        }
      }

      es.shutdown();
      try {
        es.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
      } catch (InterruptedException e) {
        LOG.error("indexing files interrupted!");
        return;
      }

    }
  }
}
