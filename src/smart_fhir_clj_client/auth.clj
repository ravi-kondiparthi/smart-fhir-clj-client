(ns smart-fhir-clj-client.auth
  (:require [smart-fhir-clj-client.fhir :as fhir]
            [smart-fhir-clj-client.request :as req]
            [smart-fhir-clj-client.util :as util]
            [clojure.tools.logging :as log]
            [clojure.spec.alpha :as spec]
            [clojure.string :as string]))


(spec/def ::access_token string?)
(spec/def ::refresh_token string?)
(spec/def ::token_type #{"Bearer"})
(spec/def ::expires_in integer?)
(spec/def ::response (spec/keys :req-un [::access_token
                                         ::token_type]
                                :opt-un [::refresh_token
                                         ::expires_in]))

(defn validate-token
  "Validate token conforms to spec"
  [token]
  (let [s ::response]
    (if (spec/valid? s token)
      token
      (let [explain (spec/explain-str s token)]
        (throw (ex-info (str "Invalid Token Response - " (util/scrub-explain-str explain)) {:retry false}))))))


(defn get-token
  "Exchange an Authorization code for a token.
   References: http://hl7.org/fhir/smart-app-launch/index.html#step-3-app-exchanges-authorization-code-for-access-token
   Note: A given developer app registered with an EHR system can have multiple redirect uris..."
  [client-id redirect-uri auth-code]
  (let [config ((keyword client-id) @fhir/conformance-map)
        token-url (:token-url config)
        base-form-params {:grant_type "authorization_code"
                          :code auth-code
                          :redirect_uri redirect-uri}
        form-params (if (= :public (:client-type config)) (assoc base-form-params :client_id client-id))]
    (log/debug "Exchanging auth code for token: " token-url)
    (let [response (try
                     (req/post token-url {:form-params form-params})
                     (catch Exception e
                       (let [{:keys [status body] :as response} (ex-data e)]
                         (throw (ex-info (str "Unexpected Error - " body) {:retry true})))))]
       (validate-token (:body response)))))
       ;(let [token (:access_token response)
       ;      expires-in-seconds (:expires_in response)
       ;      refresh-token (:refresh_token response)]))))