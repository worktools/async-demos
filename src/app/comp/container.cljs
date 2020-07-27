
(ns app.comp.container
  (:require [hsl.core :refer [hsl]]
            [respo-ui.core :as ui]
            [respo.core
             :refer
             [defcomp defeffect <> >> list-> div button textarea span input a]]
            [respo.comp.space :refer [=<]]
            [reel.comp.reel :refer [comp-reel]]
            [respo-md.comp.md :refer [comp-md]]
            [app.config :refer [dev?]]
            [respo-ui.comp :refer [comp-button]]
            [app.demos :as demos]))

(def tasks
  {"all" demos/demo-all,
   "alts" demos/demo-alts,
   "split" demos/demo-split,
   "mult" demos/demo-mult,
   "merge" demos/demo-merge,
   "mix" demos/demo-mix,
   "transduce-filter" demos/demo-transduce-filter,
   "map" demos/demo-map,
   "alt syntax" demos/demo-alt-syntax,
   "pipeline filter" demos/demo-pipeline-filter})

(defcomp
 comp-container
 (reel)
 (let [store (:store reel)
       states (:states store)
       cursor (or (:cursor states) [])
       state (or (:data states) {:content ""})]
   (div
    {:style (merge ui/global ui/column {:padding 16})}
    (div
     {}
     (a
      {:inner-text "ClojureScript core.async 丰富的语义和示例",
       :href "https://segmentfault.com/a/1190000023312457"})
     (<> "Click button and open Console for logs..."))
    (list->
     {}
     (->> tasks
          (map-indexed
           (fn [idx [title f]]
             [idx
              (comp-button
               {:text title,
                :on-click (fn [e d!] (js/console.clear) (f)),
                :style {:margin-right 8}})]))))
    (when dev? (comp-reel (>> states :reel) reel {})))))
