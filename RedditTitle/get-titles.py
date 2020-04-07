import sys
import requests
from datetime import datetime
import traceback
import json

# API setup
URL = "https://api.pushshift.io/reddit/submission/search/?after={}&before={}&sort_type=score&sort=desc&subreddit={}&limit=50"
AGENT = "UNIQUE AGENT HERE"

# Time intervals
SECONDS_PER_DAY = 3600*24 
BEGIN   = 1472169600 # Start date: 12AM GMT 
END     = 1522497600 # End date: 12PM GMT


def usage():
  print("Usage: ")
  print(sys.argv[0] + " <subreddit>")

def main():

  if len(sys.argv) != 2:
    usage()
    return -1

  subreddit = sys.argv[1]

  query_id = 0
  day = 1
  current_day = BEGIN 
  # For each day (in terms of epoch)
  while current_day < END:
    next_day = current_day + SECONDS_PER_DAY
  
  
    target = URL.format(current_day, next_day, subreddit)
    json_data = requests.get(target, headers = {'User-Agent': AGENT})
    data = json_data.json()
  
    if 'data' not in data:
      print("Failed to grab " + day)
      break

    objects = data['data']

    for item in objects:
      output = dict() 
      output["id"] = str(query_id)
      output["date"] = datetime.fromtimestamp(item['created_utc']).strftime("%Y-%m-%d")
      output["domain"] = item['domain']
      output["url"] = item['url']
      output["upvotes"] = str(item['score'])
      output["comments"] = str(item['num_comments'])
      output["reddit_title"] = item['title']

      print(json.dumps(output))
      
      query_id += 1

    current_day += SECONDS_PER_DAY
    day += 1

if __name__ == "__main__":
  main()

