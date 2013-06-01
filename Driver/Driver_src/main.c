#include <stdio.h>
#include <fuse.h>
#include <errno.h>

static int request_mkdir (const char *path, mode_t mode){
    uid_t uid;
    uid = fuse_get_context ()->uid;
    gid_t gid;
    gid = fuse_get_context ()->gid;
    //se contattando il server non c'e' nessuna risposta
    //return -ENXIO; //No souch device or address
    int ret_value;
    //ret_value = server_return;
    //if (ret_value <> 0)
    //return ret_value;
    return 0;
}

static int request_mknod (const char *path, mode_t mode, dev_t dev){
    uid_t uid;
    uid = fuse_get_context ()->uid;
    gid_t gid;
    gid = fuse_get_context ()->gid;
    //se contattando il server non c'e' nessuna risposta
    //return -ENXIO; //No souch device or address
    int ret_value;
    //ret_value = server_return;
    //if (ret_value <> 0)
    //return ret_value;
    return 0;
};

//questa struttura contiene le associazioni tra chiamate fuse e funzioni implementate
static struct fuse_operations operations = {
    .mknod = request_mknod,
    .mkdir = request_mkdir,
};

int main(int argc, char *argv[])
{
    return fuse_main(argc, argv, &operations);
}

