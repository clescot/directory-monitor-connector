# directory-monitor-connector
watch file changes in one or more directories.
Note that it does not work with remote filesystems like cifs/samba :
this implementation relies on WatchService, which is build upon inotify.
Inotify is not implemented by CIFS.