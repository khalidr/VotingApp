# Exercise

- Create a way to upload images to DropBox.  The incoming message will be of the form: 
    `{ "type": "inboundMedia", "payload": <Picture URL>, "fromNumber": "+12222222222", "toNumber": "+13333333333" }`
- Create a way to vote for pictures that exist in DropBox.
    `{ "type": "inboundText", "payload": <Picture URL>, "fromNumber": "+12222222222", "toNumber": "+13333333333" }`

The following assumptions are made:
1. Each person will only vote once.  We are not handling duplicate votes.

# Get the code

`git clone git://github.com/khalidr/VotingApp.git`

# Configuration
All configurations are in the `src/main/resources/application.conf` file.
Here are the default settings.  Please note that the `dropBox.token` property is required.

```
votingApp{
  host = localhost
  port = 2212

  db{
    host = localhost
    port = 27017
    dbName = "myDB"
  }

  dropBox{
    # Dropbox token. Required.
    # token = ""
    
    # use to get the metadata for a file
    metaDataUrl = "https://api.dropboxapi.com/1/metadata/auto/"
    
    #Url for saving file urls
    uploadUrl = "https://api.dropboxapi.com/1/save_url/auto/"
    
    # Specify the DropBox folder
    #folder = "foo"

  }
}
```

#Run it
`> sbt run`

#Run Tests
`>sbt test`

