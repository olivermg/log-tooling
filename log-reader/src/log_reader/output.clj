(ns log-reader.output
  (:refer-clojure :rename {format format-clj})
  (:require [log-reader.nodes-map :as nm]
            [zprint.core :as zp]))

(defn- ->hiccup [{:keys [callinfo children message] :as node}]
  [:div.call
   (let [{:keys [args fn time]} callinfo]
     [:span.info
      [:span.fn   (zp/zprint-str fn)]
      [:span.args (zp/zprint-str args)]
      [:span.time (zp/zprint-str time)]])
   (when message
     [:span.message
      (zp/zprint-str message)])
   (into [:div.children]
         children)])

(defn format [nodes-map]
  (let [nodes       (nm/sorted-data nodes-map)
        hiccup-data (into [:div.log-output]
                          (nm/traverse-all ->hiccup nodes))]
    (h/html hiccup-data)))
