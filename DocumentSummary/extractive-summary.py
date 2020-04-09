from newspaper import Article
import sys
import nltk
import re

def usage():
  print (sys.argv[0] + " <html file>")

# Convert instances of `"` to `'` 
def normalize(instring):
  instring = instring.replace('"', "'")
  return instring

if __name__ == "__main__":

  if len(sys.argv) != 2:
    usage()
    sys.exit(-1)

  data = open(sys.argv[1])
  text = data.read()

  article = Article(url="test", MAX_SUMMARY_SENT = 3)
  article.download(input_html=text)
  article.parse()
  # NLP for summarization
  article.nlp()
  title = normalize(article.title)
  title = "<p>" + title + "</p>"
  summary = normalize(article.summary)

  # Split summary by newline
  sentences = summary.split("\n")
  # Filter out empty sentences
  sentences = list(filter(None, sentences))

  # Short summary is the first line of the summary
  summary_short = "<p>" + sentences[0] + "</p>"
  # Long summary is the first three lines of the summary
  summary_long = ""
  for sent in sentences[:3]:
    summary_long += "<p>" + sent + "</p>"

  # Basic JSON output
  print ("{")
  print ('"id":"' + sys.argv[1] + '",')
  print ('"title":"' + title + '",')
  print ('"doc_summary_short":"' + summary_short + '",')
  print ('"doc_summary_long":"' + summary_long + '"')
  print ("}")
