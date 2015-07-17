// TraceLogger.h

#pragma once

#define TRACE_LOGGER(szClassName, szMethodSignature, szReturnType, szParameterValues) \
	TraceLogger localTraceLogger(szClassName, szMethodSignature, szReturnType, szParameterValues) 

#ifdef COMPILE_TRACELOGGER
  #define TRACELOGGER_EXPORT __declspec(dllexport)
#else
  #define TRACELOGGER_EXPORT __declspec(dllimport)
#endif

class TRACELOGGER_EXPORT TraceLogger
{
private:
	char* m_szStaticPackageName;
	char* m_szStaticClassName;
	char* m_szPackageName;
	char* m_szClassName;
	char* m_szMethodSignature;
	char* m_szReturnType;
	char* m_szParameterValues;
public:
	TraceLogger(char* szClassName, char* szMethodSignature, char* szReturnType, char* szParameterValues);
	~TraceLogger(void);
private:
	void Trace(char* szTraceType, char* szStaticPackageName, char* szStaticClassName, char* szPackageName, char* szClassName, char* szszMethodSignature, char* szszReturnType, char* szParameterValues);
};
