(ns example.service
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.body-params :as body-params]
            [cheshire.core :as json]
            [clojure.java.io :as io]
            [ring.util.response :as ring-resp]
            [clostache.parser :as clostache]
            [clojure.tools.logging :as log]
            [clojure.java.io :as io]
            [example.config :as config]
            [smart-fhir-clj-client.fhir :as sfcc]
            [smart-fhir-clj-client.auth :as sfcc-auth]))


(defn get-data
  []
  {:name ::get-data
   :enter (fn [context]
            (let [params (get-in context [:request :query-params])
                  emr-system (:emr-system params)
                  patient-id (:patient-id params)
                  token (:token params)
                  resource (:resource params)
                  config ((keyword emr-system) (config/get-config-for-emr-system))
                  client-id (:client-id config)
                  data (sfcc/get-resource-by-patient-id client-id token resource patient-id)]
              (log/info params)
              (assoc context :response {:body (json/encode data)
                                        :headers {"Content-Type" "application/json"}
                                        :status  200})))})


(defn token
  []
  {:name ::token
   :enter (fn [context]
            (let [params (get-in context [:request :query-params])
                  emr-system (:state params)
                  auth-code (:code params)
                  config ((keyword emr-system) (config/get-config-for-emr-system))
                  client-id (:client-id config)
                  token (sfcc-auth/get-token client-id (:redirect-uri config) auth-code)]
              (log/info "Authorize response:" params ", Token:" token)
              (assoc context :response (ring-resp/response
                                         (clostache/render-resource "public/select_resource_template.html"
                                                                    {:client-id client-id
                                                                     :token (:access_token token)
                                                                     :emr-data-url (:base-url config)
                                                                     :patient-id (:patient token)
                                                                     :emr-system emr-system})))))})


(defn init
  []
  {:name ::init
   :enter (fn [context]
            (assoc context :response (-> (ring-resp/resource-response "index.html" {:root "public"})
                                         (ring-resp/content-type "text/html"))))})



(defn authorize
      []
      {:name  ::authorize
       :enter (fn [context]
                (let [params (get-in context [:request :query-params])
                      emr-system (:emr-system params)
                      config ((keyword emr-system) (config/get-config-for-emr-system))
                      client-id (:client-id config)
                      base-url (:base-url config)]
                     (log/info params)
                     (sfcc/init {:client-id client-id
                                 :base-url base-url})
                     (assoc context :response (ring-resp/response
                                                (clostache/render-resource "public/authorize_template.html"
                                                                           {:client-id client-id
                                                                            :redirect-uri (:redirect-uri config)
                                                                            :auth-url (sfcc/get-authorize-url client-id)
                                                                            :emr-system emr-system})))))})


(def common-interceptors [(body-params/body-params) http/html-body])

(def routes #{["/" :get (conj common-interceptors (init))]
              ["/authorize" :get (conj common-interceptors (authorize))]
              ["/data" :get (conj common-interceptors (get-data))]
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

