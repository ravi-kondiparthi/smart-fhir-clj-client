(ns smart-fhir-clj-client.fhir
  (:require [clojure.tools.logging :as log]
            [smart-fhir-clj-client.request :as req]))

(def client-id (atom nil))
(def client-secret (atom nil))
(def base-url (atom nil))
(def auth-url (atom nil))
(def token-url (atom nil))
(def supported-resource-types (atom nil))
(def initialized (atom nil))

(defn initialize
  "sets up variables base_uri,supported resourcetypes authorizationurl , token url and mode(public or confidential)"
 ([base-url client-id-request client-secret-request]
  (print "Intialize required variables" initialized)
  (if (or (nil? base-url) (nil? client-id-request))  "metadata-url and  client-id-request are required")
  (when nil @initialized
            (locking initialized
              (when nil @initialized
                        (reset! client-id client-id-request)
                        (reset! client-secret client-secret-request)
                        (reset! auth-url client-secret-request)
                        (reset! supported-resource-types {})
                        (reset! initialized true)
                        ))))
  ([metadata-url client-id-request]
   (initialize metadata-url client-id-request nil))
  )

(defn get-data-from-conformance
  "parses metadata to extract oauth and token information"
  [conformance-map]
  (let [security (get-in conformance-map [:rest :security])
        security-extensions (->> security
                                 :extension
                                 (filter #(re-find #"oauth-uris" (:system %)))
                                 first)
        ]
      if (or (nil? security-extensions) (< (count security-extensions) 2)) ("not valid") {:token
                                                                                          (get-value-url-from-extension security-extensions "token") :authorize ((get-value-url-from-extension security-extensions "token"))}
    )
  )


(defn get-value-url-from-extension
  [extension-list type]
  (filter #(= type (:url %)) extension-list))

(defn get-metadata
  "return an map of SMART FHIR metadata details include authorize , token endpoint URLs and resource search details."
  ([]
   (if-not @base-url
     (log/error "base-url is empty. Use initialize fn to initialize with basic details")
     (get-metadata (str @base-url "/metadata"))))
  ([url]
   (if-not url
     (log/error "Metadata URL is Empty!")
     (req/get-json  url {:query-params {:_format "application/json"}}))))
