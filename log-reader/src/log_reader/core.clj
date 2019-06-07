(ns log-reader.core
  (:gen-class)
  (:require [clojure.edn :as edn]
            [clojure.pprint :as pp]
            [log-reader.formatter :as f]
            [log-reader.formatter.html :as fh]
            [log-reader.formatter.text :as ft]
            [log-reader.printer :as p]
            [log-reader.printer.http :as ph]
            [log-reader.printer.stdout :as ps]
            [log-reader.processor :as proc]
            [log-reader.reader :as r]))

(defn- getopts [args]
  (->> args
       (partition 2)
       (map (fn [[[_ & ks] v]]
              [(->> ks (apply str) keyword) v]))
       (into {})))

(defn- select-impls [{:keys [format] :as opts}]
  (case (keyword format)
    :html {:formatter (fh/construct)
           :printer   (ph/construct 8888)}
    {:formatter (ft/construct)
     :printer   (ps/construct)}))

(defn -main [& args]
  (let [opts                        (getopts args)
        {:keys [formatter printer]} (select-impls opts)
        xf                          (comp (r/eof-xf)
                                          (proc/processor-xf)
                                          (f/format-lines-xf formatter)
                                          (p/print-line-xf printer))]
    (transduce xf (constantly nil) (r/read-stream *in*))))



#_(let [#_in-str #_(str "{:trace [{:id :a :fn :afn :args :aargs :time 2} {:id :aa :fn :aafn :args :aaargs :time 3}] :x :aa}"
                  "{:trace [{:id :b :fn :bfn :args :bargs :time 1} {:id :ba :fn :bafn :args :baargs :time 2}] :x :ba}"
                  "{:trace [{:id :b :fn :bfn :args :bargs :time 3} {:id :bb :fn :bbfn :args :bbargs :time 4} {:id bba :fn :bbafn :args :bbaargs :time 5}] :x :bba}"
                  "{:trace [{:id :a :fn :afn :args :aargs :time 4} {:id :ab :fn :abfn :args :abargs :time 5}] :x :ab}"
                  "{:trace [{:id :a :fn :afn :args :aargs :time 4} {:id :ab :fn :abfn :args :abargs :time 5} {:id :aba :fn :abafn :args :abaargs :time 6}] :x :aba}"
                  "{:trace [{:id :a :fn :afn :args :aargs :time 4} {:id :ab :fn :abfn :args :abargs :time 5} {:id :abb :fn :abbfn :args :abbargs :time 7}] :x :abb}")
      in-str (slurp "testlog.log")]
  (with-in-str in-str
    (let [srv (-main)]
      (Thread/sleep 10000)
      (srv))))
