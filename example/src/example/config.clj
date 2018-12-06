(ns example.config)



(defn get-config-for-emr-system
  "Creates an api-config map, taking into account appropriate defaults and
  environment overrides."
  []
     {
      :epic {:client_id "d23d75f4-7f77-49f3-809d-ef5ca0983e9b"
             :redirect_uri "https://localhost:9307/epic/token/demo"
             :base_url "https://open-ic.epic.com/argonaut/api/FHIR/Argonaut/"}
   })


