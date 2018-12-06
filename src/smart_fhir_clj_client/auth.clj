(ns smart-fhir-clj-client.auth
  (:require [smart-fhir-clj-client.fhir :as fhir]
            [smart-fhir-clj-client.request :as req]
            [smart-fhir-clj-client.util :as util]
            [clojure.tools.logging :as log]
            [clojure.spec.alpha :as spec]
            [clojure.string :as string]))


; TODO this is a partial validation
(spec/def ::access_token string?)
(spec/def ::refresh_token string?)
(spec/def ::token_type #{"Bearer"})
(spec/def ::expires_in string?)
(spec/def ::response (spec/keys :req-un [::access_token]
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



;(fhir/initialize "https://open-ic.epic.com/argonaut/api/FHIR/Argonaut/" "d23d75f4-7f77-49f3-809d-ef5ca0983e9b")
; (get-token "d23d75f4-7f77-49f3-809d-ef5ca0983e9b" "https://localhost:9307/epic/token/demo" "3PN1py73wEmXTiQfb90XxGA4cGoZ55fMvRnNneU6t3df22I4KUJ7tT_chHjRCRlrzC4Mz77vRuLzyGF7TPyar-94vNZvVwhHX9u_P7xFCl761BQmgarN1Qt3ueb_ETZ6")

(defn get-token
  "Exchange an Authorization code for a token.

   References: http://hl7.org/fhir/smart-app-launch/index.html#step-3-app-exchanges-authorization-code-for-access-token
   Note: A given developer app registered with an EHR system can have multiple redirect uris..."
  [client-id redirect-uri auth-code]
  (let [token-url @fhir/token-url
        params {:form-params {:grant_type "authorization_code"
                              :code auth-code
                              :redirect_uri redirect-uri
                              :client_id client-id}}]; TODO only send client_id for public, omit for confidential app
    (log/debug "Exchanging auth code for token: " token-url)
    (let [response (try
                     (req/post token-url params)
                     (catch Exception e
                       (let [{:keys [status body] :as response} (ex-data e)]
                         (if (and (= 400 status))
                           (throw (ex-info (str "Bad Request - " body) {:retry true}))))))]
       (validate-token (:body response)))))
       ;(let [token (:access_token response)
       ;      expires-in-seconds (:expires_in response)
       ;      refresh-token (:refresh_token response)]))))