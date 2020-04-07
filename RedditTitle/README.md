# A small toolset for grabbing URLs and metadata from Reddit

# Credits
These tools make use of the free [Pushshift](https://github.com/pushshift/api) Reddit API.

# Grabbing data
The first step is to grab some data that matches our date range. The date range
is hardcoded into the program:
 - Start: 26/08/2016 (Epoch: 1472169600)
 - End: 31/03/2018 (Epoch:  1522497600)

We have also set the tool up to grab 50 items per day.

First we'll grab the `/r/news` subreddit
```
python3 get-titles.py news > news.json
```

Next we'll grab `/r/worldnews`
```
python3 get-titles.py worldnews > worldnews.json
```
The output data is formatted as `json` and contains the following fields:
 - `id` - A unique identifier of the Reddit item.
 - `date` - The date the article was posted to Reddit.
 - `domain` - The root domain of the article's URL.
 - `url` - The full article URL.
 - `upvotes` - The "score" of the article (higher is more popular).
 - `comments` - The number of comments on the Reddit thread.
 - `reddit_title` - The title of the submitted Reddit thread.

Each of these files should contain 29150 items. 
