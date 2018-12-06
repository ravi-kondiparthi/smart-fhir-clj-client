(ns example.service
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.body-params :as body-params]
            [cheshire.core :as json]
            [ring.util.response :as ring-resp]
            [clostache.parser :as clostache]
            [clojure.tools.logging :as log]
            [clojure.java.io :as io]
            [smart-fhir-clj-client.fhir :as sfcc]))




(def data-file (io/resource
                 "select_resource.html" ))

(defn token
  []
  {:name ::token
   :enter (fn [context]
            (let [params (get-in context [:request :query-params])
                  html-data (slurp data-file)
                  replace-data-token (clojure.string/replace html-data #"REPLACE_TOKEN" "valid-token")
                  replace-data-patient (clojure.string/replace replace-data-token #"REPLACE_PATIENT_ID" "valid-patienid")
                  replace-data-client-id (clojure.string/replace replace-data-token #"REPLACE_CLIENT_ID"
                                                                "valid-clientid")
                  ]
              (println params)
              (assoc context :response {:status 200 :body replace-data
                                        :headers {"Content-Type" "text/html"}})))})


(defn init
  []
  {:name ::init
   :enter (fn [context]
            (assoc context :response (-> (ring-resp/resource-response "index.html" {:root "public"})
                                         (ring-resp/content-type "text/html")
                                          )))})


(defn authorize
      []
      {:name  ::authorize
       :enter (fn [context]
                (let [params (get-in context [:request :query-params])
                      client-id (:client_id params)
                      base-url (:base_url params)]
                     (log/info params)
                     (sfcc/init {:client-id client-id
                                 :base-url base-url})
                     (assoc context :response (ring-resp/response
                                                (clostache/render-resource "public/authorize_template.html"
                                                                           {:client_id client-id
                                                                            :auth_url (sfcc/get-authorize-url client-id)})))))})


(def common-interceptors [(body-params/body-params) http/html-body])

(def routes #{["/" :get (conj common-interceptors (init))]
              ["/authorize" :get (conj common-interceptors (authorize))]
              ["/epic/token/demo" :get (conj common-interceptors (token))]})



;;;TODO add intereceptor to initialize app when calls are received on token on

;; Consumed by example.server/create-server
;; See http/default-interceptors for additional options you can configure
(def service {:env :dev
              ::http/routes routes
              ::http/resource-path "/public"
              ::http/type :jetty
              ::http/port 9306
              ;; Options to pass to the container (Jetty)
              ::http/container-options {:h2c? true
                                        :h2? false
                                        :keystore "./config/keystore.p12"
                                        :keystore-type "PKCS12"
                                        :key-password "qwerty"
                                        :ssl-port 9307
                                        :ssl? false}})

