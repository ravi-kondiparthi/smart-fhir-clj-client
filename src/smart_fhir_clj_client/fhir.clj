(ns smart-fhir-clj-client.fhir
  (:require [clojure.tools.logging :as log]
            [smart-fhir-clj-client.request :as req]
            [clj-hl7-fhir.core :as fhir-client]
            [clojure.string :as str]))

(def conformance-map (atom {}))

(defn get-base-url
  [client-id]
  (if client-id
    (let [base-url (:base-url ((keyword client-id) @conformance-map))]
      (if-not (or (nil? base-url) (str/blank? base-url))
        base-url
        (throw (Exception. "base-url is empty. Initialization Error. "))))
    (throw (Exception. "client-id is NULL"))))

(defn get-metadata
  "return an map of SMART FHIR metadata details include authorize , token endpoint URLs and resource search details."
  ([url]
   (if-not url
     (log/error "Metadata URL is Empty!")
     (let [response (try
                      (req/get-json  (str url "/metadata") {:query-params {:_format "application/json"}})
                      (catch Exception e
                        (throw (Exception. "Meta-Data Get Exception" e))))]
       (:body response)))))


(defn- get-value-url-from-extension
  [extension-list type]
  (:valueUri (first (filter #(= type (:url %)) extension-list))))


(defn- get-data-from-conformance
  "parses metadata to extract oauth and token information"
  [conformance-map]
  (let [security-extensions (->> (get-in conformance-map [:rest 0 :security])
                                 :extension
                                 (filter #(re-find #"oauth-uris" (:url %)))
                                 first
                                 :extension)
        urls (if (< (count security-extensions) 2)
               (log/error "Invalid security extension.")
               {:authorize (get-value-url-from-extension security-extensions "authorize")
                :token (get-value-url-from-extension security-extensions "token")})
        resource (->> (get-in conformance-map [:rest 0 :resource])
                      (mapv #(:type %)))]
    (merge urls {:resource resource})))

(defn init
  "Initialize the client"
  [input]
  (let [{:keys [base-url client-id client-secret]} input]
    (if (or (empty? input) (nil? base-url) (nil? client-id))
      (log/error "Initialization Failed. Required Field is Empty!! ")
      (when (nil? ((keyword client-id) @conformance-map))
        (locking conformance-map
          (try
            (when (nil? ((keyword client-id) @conformance-map))
              (let [meta-data (get-metadata base-url)
                    conformance (get-data-from-conformance meta-data)
                    data-map {:base-url base-url
                              :client-id client-id
                              :client-secret client-secret
                              :client-type (if client-secret "client-confidential-symmetric" "client-public")
                              :auth-url (:authorize conformance)
                              :token-url (:token conformance)
                              :support-resource-types (:resource conformance)
                              :initialize-status true}]
                 (swap! conformance-map assoc (keyword client-id) data-map)
                 (log/infof "Initialization done for client-id %s" client-id)))
            (catch Exception e
              (.printStackTrace e))))))))





(defn get-init-value
  "return `Conformance` details as a map"
  [client-id]
  (if client-id
    ((keyword client-id) @conformance-map)
    {:error "Invalid Client Id"}))



(defn get-token
  "TODO"
  []
  "lJd0Sow_-IFxerXswWfM6ZYjvNV7fkt3ONlvBWTxvLvIuIXoAlkTQXuvph6DMEAqIiwJ1xb4XuUbn8G-gMjvGWL_-8T9it6646nQtSihKYFrkfAoNuLmYVymeSHXdskk")


(defn get-resource
  "Retrieve a resource by its FHIR resource id. returns Resource or nil if not found."
  ([client-id token resource-type resource-id]
   (fhir-client/with-options {:oauth-token token}
     (fhir-client/get-resource (get-base-url client-id) resource-type resource-id)))
  ([client-id token relative-url]
   (fhir-client/with-options {:oauth-token token}
     (fhir-client/get-resource (get-base-url client-id) relative-url))))

(defn search-resource
  "Retrieve a resource by its FHIR resource type.."
  [client-id token resource-type where-condition search-params fetch-all]
  (if fetch-all
    (fhir-client/with-options {:oauth-token token}
                              (fhir-client/search (get-base-url client-id) resource-type where-condition search-params))
    (fhir-client/with-options {:oauth-token token}
                              (fhir-client/search-and-fetch (get-base-url client-id) resource-type where-condition search-params))))

