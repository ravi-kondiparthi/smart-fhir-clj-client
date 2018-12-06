(ns smart-fhir-clj-client.util
  (:require [clojure.string :as string]))



(defn scrub-explain-str
  [body]
  ;; To avoid logging sensitive information, only output the "fails spec" part of spec/explain-str (not the input value)
  (if-let [start (string/index-of body "fails spec:")]
    (if-let [end (string/index-of body "\n")]
      (subs body start end)
      (subs body start))
    "Couldn't find 'fails spec'"))