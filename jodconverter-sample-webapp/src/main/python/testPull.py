import requests
import settings
files= { 'file' : open(settings.infile, 'rb')}

r = requests.post(settings.url, files=files, stream=True)
if r.status_code != requests.codes.ok:
    print "Error: %".format(r.status_code)
else:
    chunk_size = 2048
    thumbnail = "/tmp/tmp.pdf"
    with open(thumbnail, 'wb') as f:
        for chunk in r.iter_content(chunk_size):
            f.write(chunk)

