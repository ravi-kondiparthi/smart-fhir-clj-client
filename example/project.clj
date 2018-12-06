(defproject example "0.0.1-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [io.pedestal/pedestal.service "0.5.4"]
                 [io.pedestal/pedestal.jetty "0.5.4"]
                 [cheshire "5.8.0"]
                 [de.ubercode.clostache/clostache "1.4.0"]
                 [ch.qos.logback/logback-classic "1.2.3"]
                 [org.clojure/tools.logging "0.3.1"]
                 [smart-fhir-clj-client "1.0.0-SNAPSHOT"]]

  :min-lein-version "2.0.0"
  :resource-paths ["config" "resources" "../target/*.jar"]
  ;; If you use HTTP/2 or ALPN, use the java-agent to pull in the correct alpn-boot dependency
  ;:java-agents [[org.mortbay.jetty.alpn/jetty-alpn-agent "2.0.5"]]
  :profiles {:dev {:aliases {"run-dev" ["trampoline" "run" "-m" "example.server/run-dev"]}
                   :dependencies [[smart-fhir-clj-client "1.0.0-SNAPSHOT"]
                                  [io.pedestal/pedestal.service-tools "0.5.4"]]}
             :uberjar {:aot [example.server]}}
  :main ^{:skip-aot true} example.server)

