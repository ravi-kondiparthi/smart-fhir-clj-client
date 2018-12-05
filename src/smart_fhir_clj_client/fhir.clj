(ns smart-fhir-clj-client.fhir
  (:require [clojure.tools.logging :as log]
            [smart-fhir-clj-client.request :as req]
            [clj-hl7-fhir.core :as fhir-client]))

(def conformance-map (atom {}))

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


(defn get-value-url-from-extension
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


(defn initialize
  "sets up variables base_uri,supported resource types authorization url , token url and mode(public or confidential)"
 ([base-url-request client-id-request client-secret-request]
  (if (or (nil? base-url-request) (nil? client-id-request))  "metadata-url and client-id-request are required")
  (when (nil? ((keyword client-id-request) @conformance-map))
    (locking
      (let [meta-data (get-metadata base-url-request)
            conformance (get-data-from-conformance meta-data)
            data-map {:base-url base-url-request
                      :client-id client-id-request
                      :client-secret client-secret-request
                      :client-type (if client-secret-request "client-confidential-symmetric" "client-public")
                      :auth-url (:authorize conformance)
                      :token-url (:token conformance)
                      :support-resource-types (:resource conformance)
                      :initialize-status true}]
        (swap! conformance-map assoc (keyword client-id-request) data-map))
      (log/infof "Initialization done for client ID %s" client-id-request))))


 ([base-url client-id-request]
  (initialize base-url client-id-request nil)))


(defn get-initialized-value
  []
  {:base-url @base-url
   :client-id @client-id
   :client-type @client-type
   :auth-url @auth-url
   :token-url @token-url
   :support-resource-types @supported-resource-types
   :initialize-status @initialized})

(defn get-token
  "TODO"
  []
  "lJd0Sow_-IFxerXswWfM6ZYjvNV7fkt3ONlvBWTxvLvIuIXoAlkTQXuvph6DMEAqIiwJ1xb4XuUbn8G-gMjvGWL_-8T9it6646nQtSihKYFrkfAoNuLmYVymeSHXdskk")


(defn get-resource
  "Retrieve a resource by its FHIR resource id. returns Resource or nil if not found."
  ([resource-type resource-id]
   (fhir-client/with-options {:oauth-token (get-token)}
                             (fhir-client/get-resource @base-url resource-type resource-id)))
  ([relative-url]
   (fhir-client/with-options {:oauth-token (get-token)}
                             (fhir-client/get-resource @base-url relative-url))))