
(ns app.server (:require [app.demos :as demos]))

(defn main! []
  (comment demos/demo-all)
  (comment demos/demo-alts)
  (comment demos/demo-split)
  (comment demos/demo-mult)
  (comment demos/demo-merge)
  (comment demos/demo-mix)
  (comment demos/demo-transduce-filter)
  (comment demos/demo-map)
  (comment demos/demo-alt-syntax)
  (demos/demo-pipeline-filter))

(defn reload! [] )
