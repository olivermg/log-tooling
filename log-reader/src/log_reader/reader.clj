(ns log-reader.reader
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]))

(defn- read-sexp [in]
  (try
    (edn/read {:eof ::eof} in)
    (catch Throwable e)))

(defn read-stream [in]
  (->> (repeatedly #(read-sexp in))
       (take-while #(not= % ::eof))))
