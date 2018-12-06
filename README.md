# smart-fhir-clj-client
This is a Clojure client for [FHIR](http://www.hl7.org/implement/standards/fhir/) servers supporting the [SMART on FHIR](http://docs.smarthealthit.org/) standard. 

## Supported Features
- [authorization](http://www.hl7.org/fhir/smart-app-launch/#step-3-app-exchanges-authorization-code-for-access-token)
- [read](http://hl7.org/implement/standards/fhir/http.html#read)
- [search](http://hl7.org/implement/standards/fhir/http.html#search)


## Limitations
This client only supports the standalone patient launch framework, i.e. apps intended for use by patients outside the EHR system

**References:**  
- http://hl7.org/fhir/smart-app-launch/
- http://hl7.org/fhir/smart-app-launch/#smart-authorization--fhir-access-overview

Only supports JSON (this deviates from the FHIR specification which requires servers to support both XML and JSON)

Only supports searching for all resources of a given type for the patient. 


## Roadmap
- expand searching functionality


## How to Use
### Leiningen

    [smart-fhir-clj-client "1.0.0"]

### Initialize
To initialize the client, call the `init` function passing a configuration map: 

- `:client-id` *(required)* - This is the app's client id registered with the EHR system
- `:client-secret` *(optional)* - If this is not provided, then the "public app profile" will be used. If it is provided, then the "private app profile" will be used (see http://hl7.org/fhir/smart-app-launch/#support-for-public-and-confidential-apps). 
- `:base-url` *(required)* - This is the base url for the EHR's systems FHIR server (expectation is that the server conformance can be fetched via `{{base-url}}/metadata`)

Example:

    (init {:client-id "my-test-client-id"
           :base-url "https://open-ic.epic.com/FHIR/api/FHIR/DSTU2/"})

### Authorize
After initializing the client, you will need to obtain an authorization code from the EHR system. See http://www.hl7.org/fhir/smart-app-launch/#step-1-app-asks-for-authorization for details on this step.

A helper method provides the authorization url (parsed from the EHR systems conformance):

    (get-authorize-url "my-test-client-id")
    

### Get Token 
With an authorization code, the next step is to exchange it for an OAuth token. 
     
The auth module provide a function to retrieve an OAuth token given the `client-id`, `redirect-uri`, and `authorization-code`:
 
    (get-token "my-test-client-id" "redirect-url" "authorization-code")
    
Note that the OAuth token contains the patient id. Sample OAuth token (from Epic sandbox): 

    {:access_token fIGS4bgblD-O4oTBHGHnekDx5H6AgzU_J5ezYDYcN5FrkPjLj7EXvd0mOSKMtUCZQHPfc6StoG_U1_Y9RyLb1Tra3II4B8hlBS61-GsB9lhybjDP-33NxUGah7VM11G7, 
     :token_type bearer
     :expires_in 3600
     :scope ALLERGYINTOLERANCE.READ ALLERGYINTOLERANCE.SEARCH BINARY.READ CAREPLAN.READ CAREPLAN.SEARCH CONDITION.READ CONDITION.SEARCH DEVICE.READ DEVICE.SEARCH DIAGNOSTICREPORT.READ DIAGNOSTICREPORT.SEARCH DOCUMENTREFERENCE.READ DOCUMENTREFERENCE.SEARCH GOAL.READ GOAL.SEARCH IMMUNIZATION.READ IMMUNIZATION.SEARCH MEDICATION.READ MEDICATION.SEARCH MEDICATIONORDER.READ MEDICATIONORDER.SEARCH MEDICATIONSTATEMENT.READ MEDICATIONSTATEMENT.SEARCH OBSERVATION.READ OBSERVATION.SEARCH PATIENT.READ PRACTITIONER.READ PRACTITIONER.SEARCH PROCEDURE.READ PROCEDURE.SEARCH  
     :state 00a5d4d7-2197-4d15-b5b6-95a76e1ced92 
     :patient Tbt3KuCY0B5PSrJvCu2j-PlK.aiHsu2xUjUM8bWpetXoB}
      
 
### Read
Resources can be retrieved by their FHIR resource id. The `get-resource` function takes the resource type and ID and returns a FHIR resource. Alternatively, you can specify a relative resource URL instead of separate type and ID arguments.

Examples:

    (get-resource "my-test-client-id" token :Patient "101")
    (get-resource "my-test-client-id" token "Patient" "101")
    (get-resource "my-test-client-id" token "Patient/101")


### Search
Extracting the patient id from the token, you can call the `get-resource-by-patient-id` function:

    (get-resource-by-patient-id "my-test-client-id" token "AllergyIntolerance" (:patient token))]


### Example 
Run example app with `lein`. See `example/README.md`.

## License
TODO add license