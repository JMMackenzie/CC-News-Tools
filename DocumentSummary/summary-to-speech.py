import os
import requests
import time
from lxml import etree as ET
from lxml.builder import ElementMaker
import sys
import json
from time import sleep

try:
    input = raw_input
except NameError:
    pass

def usage():
  print(sys.argv[0] + " <json file> ")

def strip_tags(string):
  return string.replace("<p>", "").replace("</p>", " ")

logger = open("audio.log", "a")

class TextToSpeech(object):

    def __init__(self, subscription_key):
        self.subscription_key = subscription_key
        self.timestr = time.strftime("%Y%m%d-%H%M")
        self.access_token = None

    def get_token(self):
        fetch_token_url = "https://australiaeast.api.cognitive.microsoft.com/sts/v1.0/issuetoken"
        headers = {
            'Ocp-Apim-Subscription-Key': self.subscription_key
        }
        response = requests.post(fetch_token_url, headers=headers)
        self.access_token = str(response.text)

    def save_audio(self, text_input, output):
        # Warning: Australia endpoint
        base_url = 'https://australiaeast.tts.speech.microsoft.com/' 
        path = 'cognitiveservices/v1'
        constructed_url = base_url + path
        headers = {
            'Authorization': 'Bearer ' + self.access_token,
            'Content-Type': 'application/ssml+xml',
            'X-Microsoft-OutputFormat': 'audio-24khz-48kbitrate-mono-mp3',
            'User-Agent': 'YourUserAgentHere'
        }

        ns = {
            None: "https://www.w3.org/2001/10/synthesis",
            "mstts": "https://www.w3.org/2001/mstts",
        }
        E = ElementMaker(namespace=ns[None], nsmap=ns)
        TTS = ElementMaker(namespace=ns['mstts'])
 
        # Use the AriaNeural model with newscast style 
        xml_body = E.speak(
          {"version": "1.0",
          "{http://www.w3.org/XML/1998/namespace}lang": "en-US"},
          E.voice(
            {"name": "en-US-AriaNeural"},
              TTS('express-as',
                  text_input,
                  style="newscast",
              )
          )
        )
        body = ET.tostring(xml_body)
        response = requests.post(constructed_url, headers=headers, data=body)
        if response.status_code == 200:
            with open(output + '.mp3', 'wb') as audio:
                audio.write(response.content)
                logger.write("OK | Status code: " + str(response.status_code) + " " + output + "\n")
        else:
            logger.write("Error | Status code: " + str(response.status_code) + " " + output + "\n")
            # 401 - try to get token again
            if response.status_code == 401:
                self.get_token()              

        logger.flush()
    


if __name__ == "__main__":

    if len(sys.argv) != 2:
      usage()
      sys.exit(-1)

    subscription_key = "your_key_here"
    app = TextToSpeech(subscription_key)
    app.get_token()

    in_file = open(sys.argv[1], 'r')
    data = json.load(in_file)
    
    for item in data:
      
      # Grab the data
      identifier = item["id"] 
      reddit_title = item["reddit_title"]
      doc_title = item["doc_title"]
      doc_summary_short = item["doc_summary_short"]
      doc_summary_long = item["doc_summary_long"]
      
      # Title
      output_name = str(identifier) + "-title"
      if os.path.isfile(output_name + ".mp3") == False:
        app.save_audio(doc_title, output_name)

      # Short summary
      output_name = str(identifier) + "-short"
      if os.path.isfile(output_name + ".mp3") == False:
        app.save_audio(doc_summary_short, output_name)

      # Long summary
      output_name = str(identifier) + "-long"
      if os.path.isfile(output_name + ".mp3") == False:
        app.save_audio(doc_summary_long, output_name)


