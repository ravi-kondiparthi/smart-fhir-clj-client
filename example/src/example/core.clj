(ns example.core
    (:require [ring.adapter.jetty :as jetty]
              [ring.util.response :as resp]))


(defn static-file
  [filename]
  (resp/resource-response filename {:root "public"}))


(defn routes [request]
  (let [uri (:uri request)]
   (case uri
         "/" (static-file "launch.html")
         "/epic/token/demo" (static-file "index.html"))))


;; Ignore favicon.ico requests
(defn wrap-ignore-favicon-request
  [handler]
  (fn [request]
      (if (= (:uri request) "/favicon.ico")
        {:status 404}
        (handler request))))

(def app
  (-> routes
      wrap-ignore-favicon-request))


(defn -main []
      (jetty/run-jetty app {:port 9307}))