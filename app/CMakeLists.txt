# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

# 隐藏符号
set(CMAKE_C_VISIBILITY_PRESET hidden)
set(CMAKE_CXX_VISIBILITY_PRESET hidden)

#打印LOG
message(STATUS "Cmake build type is: "${CMAKE_BUILD_TYPE})
message(STATUS "Cmake build android abi is: "${ANDROID_ABI})

#设置变量
set(LOCAL_PATH ${CMAKE_SOURCE_DIR}/src/main/cpp)

#引入头文件
include_directories(
        ${LOCAL_PATH}/libs/include/
        )

#导入静态库
add_library(cryptopp STATIC IMPORTED)
add_library(ssl STATIC IMPORTED)
add_library(crypto STATIC IMPORTED)
add_library(common STATIC IMPORTED)
add_library(kcp STATIC IMPORTED)
add_library(ip STATIC IMPORTED)
add_library(log4cpp STATIC IMPORTED)
add_library(protobuf STATIC IMPORTED)
add_library(leveldb STATIC IMPORTED)
add_library(uv STATIC IMPORTED)
add_library(db STATIC IMPORTED)
add_library(security STATIC IMPORTED)
add_library(transport STATIC IMPORTED)
add_library(dht STATIC IMPORTED)
add_library(network STATIC IMPORTED)
add_library(client STATIC IMPORTED)
add_library(uuid STATIC IMPORTED)
add_library(broadcast STATIC IMPORTED)
add_library(nat_traverse STATIC IMPORTED)
add_library(zlib STATIC IMPORTED)
set_target_properties(cryptopp    PROPERTIES IMPORTED_LOCATION    ${LOCAL_PATH}/libs/${ANDROID_ABI}/libcryptopp.a )
set_target_properties(ssl    PROPERTIES IMPORTED_LOCATION    ${LOCAL_PATH}/libs/${ANDROID_ABI}/libssl.a )
set_target_properties(crypto    PROPERTIES IMPORTED_LOCATION    ${LOCAL_PATH}/libs/${ANDROID_ABI}/libcrypto.a )
set_target_properties(common    PROPERTIES IMPORTED_LOCATION    ${LOCAL_PATH}/libs/${ANDROID_ABI}/libcommon.a )
set_target_properties(kcp    PROPERTIES IMPORTED_LOCATION    ${LOCAL_PATH}/libs/${ANDROID_ABI}/libkcp.a )
set_target_properties(ip    PROPERTIES IMPORTED_LOCATION    ${LOCAL_PATH}/libs/${ANDROID_ABI}/libip.a )
set_target_properties(log4cpp    PROPERTIES IMPORTED_LOCATION    ${LOCAL_PATH}/libs/${ANDROID_ABI}/liblog4cpp.a )
set_target_properties(protobuf    PROPERTIES IMPORTED_LOCATION    ${LOCAL_PATH}/libs/${ANDROID_ABI}/libprotobuf.a )
set_target_properties(leveldb    PROPERTIES IMPORTED_LOCATION    ${LOCAL_PATH}/libs/${ANDROID_ABI}/libleveldb.a )
set_target_properties(uv    PROPERTIES IMPORTED_LOCATION    ${LOCAL_PATH}/libs/${ANDROID_ABI}/libuv.a )
set_target_properties(db    PROPERTIES IMPORTED_LOCATION    ${LOCAL_PATH}/libs/${ANDROID_ABI}/libdb.a )
set_target_properties(security    PROPERTIES IMPORTED_LOCATION    ${LOCAL_PATH}/libs/${ANDROID_ABI}/libsecurity.a )
set_target_properties(transport    PROPERTIES IMPORTED_LOCATION    ${LOCAL_PATH}/libs/${ANDROID_ABI}/libtransport.a )
set_target_properties(dht    PROPERTIES IMPORTED_LOCATION    ${LOCAL_PATH}/libs/${ANDROID_ABI}/libdht.a )
set_target_properties(network    PROPERTIES IMPORTED_LOCATION    ${LOCAL_PATH}/libs/${ANDROID_ABI}/libnetwork.a )
set_target_properties(client    PROPERTIES IMPORTED_LOCATION    ${LOCAL_PATH}/libs/${ANDROID_ABI}/libclient.a )
set_target_properties(uuid    PROPERTIES IMPORTED_LOCATION    ${LOCAL_PATH}/libs/${ANDROID_ABI}/libuuid.a )
set_target_properties(broadcast    PROPERTIES IMPORTED_LOCATION    ${LOCAL_PATH}/libs/${ANDROID_ABI}/libbroadcast.a )
set_target_properties(nat_traverse    PROPERTIES IMPORTED_LOCATION    ${LOCAL_PATH}/libs/${ANDROID_ABI}/libnat_traverse.a )
set_target_properties(zlib    PROPERTIES IMPORTED_LOCATION    ${LOCAL_PATH}/libs/${ANDROID_ABI}/libzlib.a )
# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

add_library( # Sets the name of the library.
             native-lib

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
        src/main/cpp/native-lib.cpp
             )

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
        native-lib
        client
        network
        broadcast
        dht
        nat_traverse
        transport
        security
        ssl
        crypto
        zlib
        db
        ip
        kcp
        common
        log4cpp
        uv
        leveldb
        cryptopp
        protobuf
        uuid
        android
        # Links the target library to the log library
        # included in the NDK.
        ${log-lib} )