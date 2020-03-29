(ns covid-19-examples.example1
  (:require [notespace.v2.note :refer [note note-void note-hiccup note-as-hiccup note-md note-as-md]]
            [notespace.v2.live-reload]))

(note-md
 "This example is inspired by this post:
https://rviews.rstudio.com/2020/03/05/covid-19-epidemiology-with-r/
by Tim Churches.")

(note-md :setup "## Setup")

(note-void
 (require '[clojisr.v1.r :refer [r r->clj r+] :as r]
          '[clojisr.v1.applications.plotting :refer [plot->svg plot->file plot->buffered-image]]
          '[clojisr.v1.require :refer [require-r]]
          '[tech.ml.dataset :as dataset :refer [->dataset rename-columns mapseq-reader]]
          '[notespace.v2.table :as table]))

(note-void
 (require-r '[tidyr :as tidyr :refer [pivot_longer]]
            '[dplyr :as dplyr :refer [mutate]]
            '[ggplot2 :as gg :refer [ggplot geom_point geom_line xlab ylab aes]]))

(note-void
 (r '(library lubridate)))

(note-md :reading-data "## Reading data")

(note-void
 (def url
   (str "https://raw.githubusercontent.com/CSSEGISandData/"
        "COVID-19/master/csse_covid_19_data/"
        "csse_covid_19_time_series/"
        "time_series_covid19_confirmed_global.csv")))

(note-void
 (defonce raw-data
   (-> url
       ->dataset
       (rename-columns {"Province/State" :province
                        "Country/Region" :country_region}))))

(note-md :preprocessing "## Preprocessing")

(note
 (defonce data
   (-> raw-data
       (tidyr/pivot_longer '(- [:province :country_region :Lat :Long])
                           :names_to "Date"
                           :values_to "cumulative_cases")
       (dplyr/mutate :Date '(parse_date_time Date "%m/%d/%y")))))

(note-md :visualization "## Visualization")

(note-as-hiccup
 (plot->svg
  (-> data
      (r.dplyr/filter '(%in% country_region ["Indonesia" "Iran" "Italy"]))
      (gg/ggplot (gg/aes :x 'Date
                         :y 'cumulative_cases
                         :color 'country_region))
      (r+ (gg/geom_point)
          (gg/geom_line)
          (gg/xlab "x")
          (gg/ylab "y")
          (gg/scale_y_log10)))))
