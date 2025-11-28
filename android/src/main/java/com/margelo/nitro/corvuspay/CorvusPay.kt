package com.margelo.nitro.corvuspay
  
import com.facebook.proguard.annotations.DoNotStrip

@DoNotStrip
class CorvusPay : HybridCorvusPaySpec() {
  override fun multiply(a: Double, b: Double): Double {
    return a * b
  }
}
