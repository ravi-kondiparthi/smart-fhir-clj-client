# smart-fhir-clj-client
This is a Clojure client for [FHIR](http://www.hl7.org/implement/standards/fhir/) servers supporting the [SMART on FHIR](http://docs.smarthealthit.org/) standard. 

## Supported Features
- [authorization]()
- [read](http://hl7.org/implement/standards/fhir/http.html#read)
- [search](http://hl7.org/implement/standards/fhir/http.html#search)


## Limitations
This client only supports the standalone patient launch framework, i.e. apps intended for use by patients outside the EHR system

**References:**  
- http://hl7.org/fhir/smart-app-launch/
- http://hl7.org/fhir/smart-app-launch/#smart-authorization--fhir-access-overview

Only supports JSON (this deviates from the FHIR specification which requires servers to support both XML and JSON)


## Roadmap
TBD


## How to Use
### Leiningen

    [smart-fhir-clj-client "1.0.0"]

### Initialize
To initialize the client, call the `init` function passing a configuration map: 

- `:client-id` *(required)* - This is the app's client id registered with the EHR system
- `:client-secret` *(optional)* - If this is not provided, then the "public app profile" will be used. If it is provided, then the "private app profile" will be used (see http://hl7.org/fhir/smart-app-launch/#support-for-public-and-confidential-apps). 
- `:metadata-url` *(required)* - This is the url for the EHR's systems `/metadata` endpoint

Example:

    (init {:client-id "my-test-client-id"
           :metadata-url https://open-ic.epic.com/argonaut/api/FHIR/Argonaut/metadata})

### Read
TBD

### Search
TBD

### Example 
Run example app with lein. See `example/README.md`.

## License
TODO