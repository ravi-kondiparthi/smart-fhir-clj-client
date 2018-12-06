(ns smart-fhir-clj-client.request
  ""
  (:require [clj-http.client :as client]
            [clojure.tools.logging :as log]))



(def default-client-options {:conn-timeout   30000    ;; time to establish a connection
                             :socket-timeout 30000})  ;; connection established, waiting for data


(defn get-json
  ([url] (get-json url {}))
  ([url request-params]
   (client/get url (merge {:as :json}
                          default-client-options
                          request-params))))


(defn post
  ([url] (post url {}))
  ([url request-params]
   (client/post url (merge {:as :json}
                           default-client-options
                           request-params))))