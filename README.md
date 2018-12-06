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
           :base-url https://open-ic.epic.com/argonaut/api/FHIR/Argonaut/metadata})

### Authorize
After initializing the client, you will need to obtain an authorization code from the EHR system. To ass


    (get-authorize-url)
 
### Read
Resources can be retrieved by their FHIR resource id. The `get-resource` function takes the resource type and ID and returns a FHIR resource. Alternatively, you can specify a relative resource URL instead of separate type and ID arguments.

Examples:

    (get-resource "" :Patient "101")
    (get-resource "Patient" "101")
    (get-resource "Patient/101")

Example including sample response from Epic Sandbox:

    (get-resource "my-client-id" "valid-oauth-token" :patient "Tbt3KuCY0B5PSrJvCu2j-PlK.aiHsu2xUjUM8bWpetXoB")
    
    {:address [{:use "home", :line ["1979 Milky Way Dr."], :city "Verona", :state "WI", :postalCode "53593", :country "US"}
               {:use "temp",
                :line ["5301 Tokay Blvd"],
                :city "MADISON",
                :state "WI",
                :postalCode "53711",
                :country "US",
                :period {:start "2011-08-04T00:00:00Z", :end "2014-08-04T00:00:00Z"}}],
     :deceasedBoolean false,
     :name [{:use "usual", :text "Jason Argonaut", :family ["Argonaut"], :given ["Jason"]}],
     :birthDate "1985-08-01",
     :careProvider [{:display "Physician Family Medicine",
                     :reference "https://open-ic.epic.com/Argonaut/api/FHIR/DSTU2/Practitioner/T3Mz3KLBDVXXgaRoee3EKAAB"}],
     :resourceType "Patient",
     :extension [{:url "http://hl7.org/fhir/StructureDefinition/us-core-race",
                  :valueCodeableConcept {:text "Asian",
                                         :coding [{:system "2.16.840.1.113883.5.104", :code "2028-9", :display "Asian"}]}}
                 {:url "http://hl7.org/fhir/StructureDefinition/us-core-ethnicity",
                  :valueCodeableConcept {:text "Not Hispanic or Latino",
                                         :coding [{:system "2.16.840.1.113883.5.50",
                                                   :code "2186-5",
                                                   :display "Not Hispanic or Latino"}]}}
                 {:url "http://hl7.org/fhir/StructureDefinition/us-core-birth-sex",
                  :valueCodeableConcept {:text "Male",
                                         :coding [{:system "http://hl7.org/fhir/v3/AdministrativeGender",
                                                   :code "M",
                                                   :display "Male"}]}}],
     :active true,
     :communication [{:preferred true,
                      :language {:text "English",
                                 :coding [{:system "urn:oid:2.16.840.1.113883.6.99", :code "en", :display "English"}]}}],
     :id "Tbt3KuCY0B5PSrJvCu2j-PlK.aiHsu2xUjUM8bWpetXoB",
     :identifier [{:use "usual", :system "urn:oid:1.2.840.114350.1.13.327.1.7.5.737384.0", :value "E3826"}
                  {:use "usual", :system "urn:oid:1.2.3.4", :value "203579"}],
     :telecom [{:system "phone", :value "608-271-9000", :use "home"}
               {:system "phone", :value "608-771-9000", :use "work"}
               {:system "phone", :value "608-771-9000", :use "mobile"}
               {:system "fax", :value "608-771-9000", :use "home"}
               {:system "phone",
                :value "608-771-9000",
                :use "temp",
                :period {:start "2011-08-04T00:00:00Z", :end "2014-08-04T00:00:00Z"}}
               {:system "email", :value "open@epic.com"}],
     :gender "male",
     :maritalStatus {:text "Single",
                     :coding [{:system "http://hl7.org/fhir/ValueSet/marital-status", :code "S", :display "Never Married"}]}}


#### Issues
(get-resource "Foo" "1")
NullPointerException   clojure.lang.Reflector.invokeInstanceMethod (Reflector.java:26)


(get-resource :encounter "Tbt3KuCY0B5PSrJvCu2j-PlK.aiHsu2xUjUM8bWpetXoB")
NullPointerException   clojure.lang.Reflector.invokeInstanceMethod (Reflector.java:26)

### Search

    (fhir-client/with-options {:oauth-token (get-token)}
      (fhir-client/search @base-url :patient []))
    =>
    {:resourceType "Bundle",
     :type "searchset",
     :total 0,
     :entry [{:search {:mode "outcome"},
              :resource {:resourceType "OperationOutcome",
                         :id "2601055",
                         :issue [{:severity "warning",
                                  :code "informational",
                                  :details {:text "Resource request returns no results.",
                                            :coding [{:system "urn:oid:1.2.840.114350.1.13.0.1.7.2.657369",
                                                      :code "4101",
                                                      :display "Resource request returns no results."}]}}
                                 {:severity "warning",
                                  :code "informational",
                                  :details {:text "This response includes information available to the authorized user at the time of the request. It may not contain the entire record available in the system.",
                                            :coding [{:system "urn:oid:1.2.840.114350.1.13.0.1.7.2.657369",
                                                      :code "4119",
                                                      :display "This response includes information available to the authorized user at the time of the request. It may not contain the entire record available in the system."}]}}]}}],
     :link [{:relation "self", :url "https://open-ic.epic.com/Argonaut/api/FHIR/Argonaut/Patient"}]}


### Example 
Run example app with lein. See `example/README.md`.

## License
TODO