# Some tools for building document summaries and speech summaries

## Extracting document text and summaries

### Requirements
These scripts make use of the [newspaper3k](https://github.com/codelucas/newspaper/) Python tool.
```
pip3 install newspaper3k
```
We used Python 3.6.7.

### Extract document text
In order to extract the text from a document, you will need the raw HTML representation. 
There are a number of ways to get the raw HTML representation of a given document, and we
provide one such method in our [Indri example](http://github.com/JMMackenzie/CC-News-Tools/IndriIndex/README.md).

Let us assume we have the HTML body of a given document in the file `example.html`.
To extract the full text of a document into a single, simple json object, use the `html-to-json.py` tool:
```
python html-to-json.py example.html
```
Example output (truncated for readability):
```
{
"id":"example.html",
"data":"ON FEBRUARY 3, A TELEVISION VIEWER in southeast Texas submitted a complaint to the Federal Communications Commission about CNN ..."
}
```

### Extract the first sentences of a document
To extract the opening sentences of a document, use the `opening-text.py` tool:
```
python opening-text.py example.html
```
Example output (truncated for readability):
```
{
"id":"example.html",
"tittle":"<p>TV viewers have been sending ‘fake news’ complaints to the FCC</p>",
"doc_summary_short":"<p>ON FEBRUARY 3, A TELEVISION VIEWER in southeast Texas submitted a complaint to the Federal Communications Commission about CNN ...</p>",
"doc_summary_long":"<p>ON FEBRUARY 3, A TELEVISION VIEWER in southeast Texas submitted a complaint to the Federal Communications Commission about CNN ...</p><p> ... </p><p> ... </p>"
}
```
Note that `<p>...</p>` tags are used to explicitly denote sentence boundaries, and that the tool is currently configured to output
the first sentence as the short summary, and the first three sentences as the long summary.

### Get an extractive summary of a document
Use the `extractive-summary.py` tool:
```
python extractive-summary.py example.html
```
Example output (truncated):
```
{
"id":"example.html",
"title":"<p>TV viewers have been sending ‘fake news’ complaints to the FCC</p>",
"doc_summary_short":"<p>Below is a selection of the complaints, each accompanied by its place of origin and the date it was filed.</p>",
"doc_summary_long":"<p>Below is a selection of the complaints, each accompanied by its place of origin and the date it was filed.</p><p> ... </p><p> ... </p>"
}
```
## Summary data to speech
The `summary-to-speech.py` script reads through a `json` file containing
the document summary information, and generates a spoken version of the
document title, short summary, and long summary. 

### Requirements
This script uses Microsoft Azure's Cognitive Services 
[Text-to-Speech REST API](https://docs.microsoft.com/en-us/azure/cognitive-services/speech-service/rest-text-to-speech), 
and hence requires an API key.

### Example
Given the following (simplified) json file:
```
{
  "id": 15335,
  "doc_title": "Redcar rescue: Four saved from sea by human chain",
  "doc_summary_short": "A man fell from the slipway at Dundas Street in Redcar at about 16:00 BST on Good Friday and was battered by large waves close to the sea wall.",
  "doc_summary_long": "A man fell from the slipway at Dundas Street in Redcar at about 16:00 BST on Good Friday and was battered by large waves close to the sea wall. A man and a woman who attempted to rescue the pair then also ended up in the sea. Redcar RNLI said it was only through 'good fortune' they were all saved and urged against copycat behaviour.",
}
```

The script would generate three output files, namely:
 - `15335-title.mp3` containing audio of the `doc_title`
 - `15335-short.mp3` containing audio of the `doc_summary_short`
 - `15335-long.mp3` containing audio of the `doc_summary_long`

### Batch Processing
To process a batch of items, simply pass a json file containing multiple entries.
The script will work through each json item.

### Authentication Issues
Occasionally, the script will lose authentication and return a `401` error. If
this occurs, the script will automatically re-authenticate. However, after
processing, there may be a few missing files. You can simply re-run the script
to grab these missing files (as the script automatically skips files it has
already converted).  
