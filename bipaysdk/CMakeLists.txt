cmake_minimum_required(VERSION 3.4.1)

add_library( native-lib SHARED src/main/cpp/native-lib.cpp )

include_directories(src/main/cpp)

add_library(bipay SHARED IMPORTED)
set_target_properties(bipay PROPERTIES IMPORTED_LOCATION
                      ${PROJECT_SOURCE_DIR}/libs/${ANDROID_ABI}/libbipay.so )

find_library( log-lib log )

target_link_libraries(
                        native-lib
                        bipay
                        ${log-lib}
                        )
