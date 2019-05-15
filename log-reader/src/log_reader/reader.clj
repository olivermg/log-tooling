(ns log-reader.reader
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]))

(defn read-stream [in]
  (-> (repeatedly #(edn/read {:eof ::eof} in))
      (take-while #(not= % ::eof))))
