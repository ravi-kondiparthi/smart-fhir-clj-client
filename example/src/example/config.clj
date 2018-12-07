(ns example.config)



(defn get-config-for-emr-system
  "Creates an api-config map, taking into account appropriate defaults and
  environment overrides."
  []
  {:epic {:client-id "d23d75f4-7f77-49f3-809d-ef5ca0983e9b"
          :redirect-uri "https://localhost:9307/epic/token/demo"
          :base-url "https://open-ic.epic.com/FHIR/api/FHIR/DSTU2"}
   :hspc {:client-id "978599b0-c31f-402f-930a-c8b3f573ec86"
          :redirect-uri "https://localhost:9307/epic/token/demo"
          :base-url "https://api-v5-dstu2.hspconsortium.org/ravSandBox/data"}})
