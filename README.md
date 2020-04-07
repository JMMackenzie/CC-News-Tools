# CC-News-Tools
Tools relating to the CC-News-En Collection.


## Usage
If you use the `CC-News-En` collection, please cite the overview paper:
```
@article{abc-123,
 title = {CC-News-En: A Large English Newswire Corpus}
}
```

If you are just looking for the raw corpus and query data, you can download it 
from [AARNET CloudStor](https://cloudstor.aarnet.edu.au/plus/s/M8BvXxe6faLZ4uE).

## Credits
The CC-News-En corpus is derived from the CC-News crawl, which is made
possible by the [Common Crawl Foundation](https://commoncrawl.org/).

## TikaLuceneWarc
Based on the original [TikaLuceneWarc](https://github.com/mpetri/TikaLuceneWarc)
library, this contains the code required to process the corpus, including
both downloading the raw data and doing the English filtering.

## IndriIndex
Tools for building an [Indri](http://www.lemurproject.org/indri/) index over 
CC-News-En. 

## RedditTitle
Scripts for crawling temporally relevant Reddit Titles. These tools are based
on the [Pushshift](https://github.com/pushshift/api) Reddit API.

## DocumentSummary
Scripts and tools for generating document summary information. Also includes
scripts for generating audio transcripts of summaries.

