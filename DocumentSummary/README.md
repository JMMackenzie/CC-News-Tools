# Some tools for building document summaries and speech summaries


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
