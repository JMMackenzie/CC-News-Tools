# Building an Indri Index over CC-News-En

## Requirements
The following Indri build has been tested (and is working) with
Indri 5.16 with gcc version 6.3.1 20170216 (Red Hat 6.3.1-3).
We have noted some issues with segmentation faults if a more recent
version of gcc is used (such as gcc version 8.3.1 20190311 (Red Hat 8.3.1-3)).
Please file any appropriate tickets with the
[Lemur Project](https://sourceforge.net/p/lemur/bugs/).

## Build Indri
Download and build Indri. We'll use the mirror on Github hosted by
[Luke Gallagher](https://github.com/lgrz).
```
git clone https://github.com/lgrz/indri
cd indri
mkdir -p obj contrib/antlr/obj contrib/lemur/obj contrib/xpdf/obj contrib/zlib/obj
./configure
make
```

## Index the Corpus
Look at the `indri-index.param` file and check the path to the raw CC-News-En
WARC collection (the `<path></path>` value). 
Adjust the output path (the `<index></index>` value). Then, run IndriBuildIndex.
```
/path/to/indri/buildindex/IndriBuildIndex indri-index.param 
```

Note that the output path should have at least 1.5TB of storage available.

## Dump the vocabulary
The vocabulary can be dumped using the `dumpindex` tool.
The output format is: `<term> <collection frequency> <document frequency>`
```
/path/to/indri/dumpindex/dumpindex /path/to/Indri-CC-News-En/ v > cc-news-vocab.txt
```

## Dump the document map
The document mapping is useful, as interacting with `dumpindex` requires the
underlying numeric document identifiers. To get a file which maps the
textual document identifiers to the numeric identifiers, we can use the
`docmap` tool. Please follow the simple build instructions in the `docmap`
directory to build the tool. Then you can dump the document map in the form
`<integer id> <document identifier>`
```
./docmap/docmap /path/to/Indri-CC-News-EN > cc-news-docids.txt

```

## Dump raw documents of interest
In order to facilitate summarization, some users may be interested in dumping
the raw HTML of some documents. This can be achieved via the `dumpindex` tool.
Assume we want to dump the raw HTML from a particular document. First we find
the integer document identifier from the document map (see the previous section).
Assume for the example that we are dumping the contents of document `12345`.
```
/path/to/indri/dumpindex/dumpindex /path/to/Indri-CC-News-En/ dt 12345 > 12345.html
```

