(ns billsplit-clojure.model
  (:require [schema.core :as s])
  (:import (clojure.lang Ratio)))

(s/defschema Quantity (s/pred pos-int?))

(s/defschema Product
  {:name                      s/Str
   :quantity                  Quantity
   :price                     BigDecimal
   (s/optional-key :fraction) (s/maybe (s/either Ratio s/Int))
   :id                        s/Int})

(s/defschema Person
  {:name                    s/Str
   :products                [Product]
   (s/optional-key :to-pay) (s/maybe BigDecimal)})

(s/defschema Bill
  {s/Str Person})
