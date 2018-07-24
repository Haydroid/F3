#pragma once


class F2IDCReader
{
public:
	F2IDCReader();
	~F2IDCReader();

    int connect(char *portName, speed_t speed);
    int disconnect();
    int execute(BYTE *cmpData, int cmpLength, int timeOut, int rdtOffset,bool bStatus );
	int sendCommand(BYTE *cmpData, int cmpLength);

	pthread_mutex_t mutex;
	BYTE *rdtptr;
	int rdtlen;

private:

    int recvACK();
    int sendENQ();
    int recvResponse(int timeOut, int rdtOffset, bool bStatus);
    int readData(BYTE *buffer, int bytesToRead, fd_set *fds);
    int writeData(BYTE *buffer, int bytesToWrite);
    int waitBytesAvailable(int timeOut, fd_set *fds);
    BYTE getBCC(BYTE *buffer, int length);
    int configPort(int fd, speed_t speed);
    void logCommData(bool isSend, BYTE *buffer, int length);

private:
    enum { MAX_MSGSIZE = 65536 };
	enum { MAX_MSG_LENGTH = 1024 };
	BYTE m_msgBuffer[MAX_MSGSIZE];
    int m_fd;
	bool isConnect;
};