#项目信息
project(Hook)
#将当前目录下的所有源文件，并将名称保存到 RootSrc 变量
aux_source_directory(. RootSrc)

add_library(Hook SHARED ${RootSrc})

target_link_libraries(Hook android log dl)