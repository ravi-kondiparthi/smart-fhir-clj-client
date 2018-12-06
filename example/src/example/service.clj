(ns example.service
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.body-params :as body-params]
            [cheshire.core :as json]
            [ring.util.response :as ring-resp]
            [clostache.parser :as clostache]
            [clojure.tools.logging :as log]
            [smart-fhir-clj-client.fhir :as sfcc]))


(defn token
  []
  {:name ::token
   :enter (fn [context]
            (let [params (get-in context [:request :query-params])]
              (println params)
              ; TODO next step is to exchange auth code for token
              (assoc context :response {:body (json/encode params)
                                        :headers {"Content-Type" "application/json"}
                                        :status  200})))})


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
                      client-id (:client_id params)
                      redirect-uri (:redirect_uri params)
                      base-url (:base_url params)]
                     (log/info params)
                     (sfcc/init {:client-id client-id
                                 :base-url base-url})
                     (assoc context :response (ring-resp/response
                                                (clostache/render-resource "public/authorize_template.html"
                                                                           {:client_id client-id
                                                                            :redirect_uri redirect-uri
                                                                            :auth_url (sfcc/get-authorize-url client-id)})))))})


(def common-interceptors [(body-params/body-params) http/html-body])
(def routes #{["/" :get (conj common-interceptors (init))]
              ["/authorize" :get (conj common-interceptors (authorize))]
              ["/epic/token/demo" :get (conj common-interceptors (token))]})



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

