(ns smart-fhir-clj-client.fhir
  (:require [clojure.tools.logging :as log]))

(def client-id (atom nil))
(def client-secret (atom nil))
(def base-url (atom nil))
(def auth-url (atom nil))
(def supported-resource-types (atom nil))
(def initialized (atom nil))

(defn initialize
  "sets up variables base_uri,supported resourcetypes authorizationurl , token url and mode(public or confidential)"
  [metadata-url client-id-request client-secret-request]
  (print "Intialize required variables" initialized)
  (when nil @initialized
        (locking initialized
                (when nil @initialized
                       (reset! client-id client-id-request)
                       (reset! client-secret client-secret-request)
                       (reset! base-url client-secret-request)
                       (reset! auth-url client-secret-request)
                       (reset! supported-resource-types {})
                       (reset! initialized true)
                       )))
  )