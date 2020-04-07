# A Tika-Lucene WARC parser for the CC-NEWS crawl

# Credits
This version of TikaLuceneWarc is modified from the original codebase
[TikaLuceneWarc](https://github.com/mpetri/TikaLuceneWarc), created by
[Matthias Petri](https://github.com/mpetri).

# Requirements
This codebase requires the pre-trained Apache OpenNLP language detection
model, which has been included by default in the `external/` directory.
More information can be found at [OpenNLP](http://opennlp.apache.org/models.html).

# Installation

```
mvn clean package appassembler:assemble
```

# Getting the collection from AWS

This requires at least 1TB per year (crawl started 26/08/16)

```
apt-get install awscli
```

List the precise files required to replicated `CC-News-En`
```
aws s3 ls --no-sign-request --recursive s3://commoncrawl/crawl-data/CC-NEWS/2016 >> check.txt
aws s3 ls --no-sign-request --recursive s3://commoncrawl/crawl-data/CC-NEWS/2017 >> check.txt
aws s3 ls --no-sign-request --recursive s3://commoncrawl/crawl-data/CC-NEWS/2018/01/ >> check.txt
aws s3 ls --no-sign-request --recursive s3://commoncrawl/crawl-data/CC-NEWS/2018/02/ >> check.txt
aws s3 ls --no-sign-request --recursive s3://commoncrawl/crawl-data/CC-NEWS/2018/03/ >> check.txt

wc -l check.txt
2290 check.txt

head -n 1 check.txt
2016-08-27 00:05:02 1073755363 crawl-data/CC-NEWS/2016/08/CC-NEWS-20160826124520-00000.warc.gz

tail -n 1 check.txt
2018-04-01 11:05:03 1072758462 crawl-data/CC-NEWS/2018/03/CC-NEWS-20180331191315-00143.warc.gz
```

Retrieve the precise files required to replicate `CC-News-En` - will require around
2.5 TB of storage. We'll store the files at `/path/to/raw/collection/` for this
example.
```
mkdir /path/to/raw/collection/
aws s3 cp --no-sign-request --recursive s3://commoncrawl/crawl-data/CC-NEWS/2016 /path/to/raw/collection/
aws s3 cp --no-sign-request --recursive s3://commoncrawl/crawl-data/CC-NEWS/2017 /path/to/raw/collection/
aws s3 cp --no-sign-request --recursive s3://commoncrawl/crawl-data/CC-NEWS/2018/01/ /path/to/raw/collection/
aws s3 cp --no-sign-request --recursive s3://commoncrawl/crawl-data/CC-NEWS/2018/02/ /path/to/raw/collection/
aws s3 cp --no-sign-request --recursive s3://commoncrawl/crawl-data/CC-NEWS/2018/03/ /path/to/raw/collection/
```

# Output format
The tool provided will go through the raw collection, read the gzipped warc files
into memory, decompress them, filter out non English documents, and then re-pack
into a new WARC file. The filtered WARC data requires about 1TB of disk space.

Note that an input file such as `CC-NEWS-20180331191315-00143.warc.gz`
will have its filtered output written to `CC-NEWS-20180331191315-00143_ENG.warc.gz`

# Generating CC-News-En
Now, run the binary to generate the English collection. Note that this process
may take a few days, so use `tmux` or `screen`.

```
cd TikaLuceneWarc
mvn clean package appassembler:assemble
./target/appassembler/bin/TikaLuceneWarc -input /path/to/raw/collection/ -lm ./external/apache-opennlp-1.8.4/bin/langdetect-183.bin -output /path/to/english/collection/
```

Once processing is complete, the output directory should contain 2289 files,
one less than the number of input files. This is because processing the 
`CC-NEWS-20170812163812-00038.warc.gz` raises an uncaught exception and is
skipped. While we could fix this bug, it would result in a slightly different
corpus to the one described, so we opted to leave the program as-is.


