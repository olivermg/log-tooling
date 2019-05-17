(ns log-reader.output
  (:refer-clojure :rename {format format-clj})
  (:require [log-reader.nodes-map :as nm]
            [zprint.core :as zp]))

(defn- ->hiccup [{:keys [callinfo children message] :as node}]
  [:div.node
   (let [{:keys [args fn time]} callinfo]
     [:div.callinfo
      [:div.fn [:pre (zp/zprint-str fn)]]
      [:div.args [:pre (zp/zprint-str args)]]
      [:div.time [:pre (zp/zprint-str time)]]])
   (when message
     [:div.message
      [:pre (zp/zprint-str message)]])
   (into [:div.children]
         children)])

(defn format [nodes-map]
  (let [nodes       (nm/sorted-data nodes-map)
        hiccup-data (into [:div.log-output]
                          (nm/traverse-all ->hiccup nodes))]
    (h/html hiccup-data)))
