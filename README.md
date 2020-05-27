# CC-News-Tools
This repo contains a number of tools that assist in working with the
*Common Crawl News* collection. In particular, the *CC-News-En* corpus
can be reproduced or extended using these tools. 

## Usage
If you use the `CC-News-En` collection, please cite the overview paper:
```
@inproceedings{ccnews2020,
 title = {CC-News-En: A Large English Newswire Corpus},
 authors = {},
 booktitle = {},
 pages = {},
 year = {2020},
}
```

If you are just looking for the raw corpus and query data, you can download it 
from [AARNET CloudStor](https://cloudstor.aarnet.edu.au/plus/s/M8BvXxe6faLZ4uE).

## Credits
The CC-News-En corpus is derived from the CC-News crawl, which is made
possible by the [Common Crawl Foundation](https://commoncrawl.org/).

## Building CC-News-En from scratch
**Located in the TikaLuceneWarc directory.**
Based on the original [TikaLuceneWarc](https://github.com/mpetri/TikaLuceneWarc)
library, this contains the code required to process the corpus, including
both downloading the raw data and doing the English filtering.

## Building an Indri Index over CC-News-En
**Located in the IndriIndex directory.**
Tools for building an [Indri](http://www.lemurproject.org/indri/) index over 
CC-News-En. 

## Getting popularity data from Reddit
**Located in the RedditTitle directory.**
Scripts for crawling temporally relevant Reddit Titles. These tools are based
on the [Pushshift](https://github.com/pushshift/api) Reddit API.

## Building document summaries
**Located in the DocumentSummary directory.**
Scripts and tools for generating document summary information. Also includes
scripts for generating audio transcripts of summaries.

