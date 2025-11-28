#include <jni.h>
#include "corvuspayOnLoad.hpp"

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void*) {
  return margelo::nitro::corvuspay::initialize(vm);
}
