(ns example.service
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.body-params :as body-params]
            [cheshire.core :as json]
            [ring.util.response :as ring-resp]))


(defn token
  []
  {:name ::token
   :enter (fn [context]
            (let [params (get-in context [:request :query-params])]
              (println params)
              (assoc context :response {:body (json/encode params)
                                        :headers {"Content-Type" "application/json"}
                                        :status  200})))})

(defn index
  []
  {:name ::index
   :enter (fn [context]
            (assoc context :response (-> (ring-resp/resource-response "index.html" {:root "public"})
                                         (ring-resp/content-type "text/html"))))})


(def common-interceptors [(body-params/body-params) http/html-body])
(def routes #{["/" :get (conj common-interceptors (index))]
              ["/epic/token/demo" :get (conj common-interceptors (token))]})



;; Consumed by example.server/create-server
;; See http/default-interceptors for additional options you can configure
(def service {:env :dev
              ::http/routes routes

              ;; Root for resource interceptor that is available by default.
              ::http/resource-path "/public"
              ::http/type :jetty
              ;;::http/host "localhost"
              ::http/port 9306
              ;; Options to pass to the container (Jetty)
              ::http/container-options {:h2c? true
                                        :h2? false
                                        :keystore      "./config/keystore.p12"
                                        :keystore-type "PKCS12"
                                        :key-password  "qwerty"
                                        :ssl-port 9307
                                        :ssl? false}})

