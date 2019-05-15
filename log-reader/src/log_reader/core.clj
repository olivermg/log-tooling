(ns log-reader.core
  (:gen-class)
  (:require [log-reader.reader :as r]))

(defn -main [& args]
  (r/read-stream *in*))
