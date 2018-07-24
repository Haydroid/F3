#pragma once

#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include <unistd.h>
#include <fcntl.h>
#include <termios.h>
#include <memory.h>
#include <pthread.h>
#include <sys/stat.h>
#include <assert.h>

#include "android/log.h"

#define TAG "vtm-api"
#define LOGD(fmt, args...) __android_log_print(ANDROID_LOG_DEBUG, TAG, fmt, ##args)
#define LOGE(fmt, args...) __android_log_print(ANDROID_LOG_ERROR, TAG, fmt, ##args)

typedef unsigned char BYTE;


static __inline__ int __tcgetattr(int fd, termios *s)
{
    return ioctl(fd, TCGETS, s);
}

static __inline__ int __tcsetattr(int fd, int __opt, const termios *s)
{
    return ioctl(fd, __opt, (void *)s);
}

static __inline__ int __tcflush(int fd, int __queue)
{
    return ioctl(fd, TCFLSH, (void *)(intptr_t)__queue);
}

static __inline__ speed_t __cfgetospeed(const termios *s)
{
    return (speed_t)(s->c_cflag & CBAUD);
}

static __inline__ int __cfsetospeed(termios *s, speed_t  speed)
{
    s->c_cflag = (s->c_cflag & ~CBAUD) | (speed & CBAUD);
    return 0;
}

static __inline__ speed_t __cfgetispeed(const termios *s)
{
    return (speed_t)(s->c_cflag & CBAUD);
}

static __inline__ int __cfsetispeed(termios *s, speed_t  speed)
{
    s->c_cflag = (s->c_cflag & ~CBAUD) | (speed & CBAUD);
  return 0;
}

static __inline__ void __cfmakeraw(termios *s)
{
    s->c_iflag &= ~(INPCK|IGNBRK|BRKINT|PARMRK|ISTRIP|INLCR|IGNCR|ICRNL|IXON);//INPCK
    s->c_oflag &= ~OPOST;
    s->c_lflag &= ~(ECHO|ECHONL|ICANON|ISIG|IEXTEN);
    s->c_cflag |= PARENB;
    s->c_cflag &= ~PARODD;
    s->c_cflag &= ~CSIZE;
    s->c_cflag |= CS8;
}