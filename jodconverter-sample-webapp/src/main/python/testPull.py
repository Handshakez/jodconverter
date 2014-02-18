import requests
import time
import settings
files= { 'file' : open(settings.infile, 'rb')}

start = time.time()
print "Staring request..."
r = requests.post(settings.url, files=files, stream=True)
end = time.time()
print "Elapsed: {}".format(end - start)
if r.status_code != requests.codes.ok:
    print "Error: {}".format(r.status_code)
else:
    chunk_size = 2048
    thumbnail = "/tmp/tmp.png"
    with open(thumbnail, 'wb') as f:
        for chunk in r.iter_content(chunk_size):
            f.write(chunk)

