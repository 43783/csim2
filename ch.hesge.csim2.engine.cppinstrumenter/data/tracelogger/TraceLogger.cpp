// This is the main DLL file.

#include "stdafx.h"
#include "TraceLogger.h"

// File used to write log messages
static FILE *fStream; 

// Keep track of current session
static int iFileSessionTracker = 0; 

// Target filename used to trace
static char* szTraceFilename = "csim2-trace.v2.log"; 

// TraceLogger constructor
// This method is invoked while constructing the logger on the stack.
// That is when the method scope of the method to created.
TraceLogger::TraceLogger(char* szClassName, char* szMethodSignature, char* szReturnType, char* szParameterValues)
{
	m_szStaticPackageName = "";
	m_szStaticClassName = "";
	m_szPackageName = "";
	m_szClassName = szClassName;
	m_szMethodSignature = szMethodSignature;
	m_szReturnType = szReturnType;
	m_szParameterValues = szParameterValues;

	Trace("ENTER", m_szStaticPackageName, m_szStaticClassName, m_szPackageName, m_szClassName, m_szMethodSignature, m_szReturnType, m_szParameterValues);
}

// TraceLogger destructor
// This method is invoked while the logger is destroyed from the stack.
// That is when the method scope of the method to instrument is reached.
TraceLogger::~TraceLogger(void)
{
	Trace("EXIT", m_szStaticPackageName, m_szStaticClassName, m_szPackageName, m_szClassName, m_szMethodSignature, m_szReturnType, "");
}

/**
 * Create a trace line within the trace file
 * 
 * Trace Execution Format (v2.0 - Format 2010):
 *
 * Method entering:
 * [staticPackageName] [staticClassName] [packageName] [className] "[" [threadNumber] "]" [szMethodSignature] "AS" [returnedType] "[" [timeStmp] "]" [paramValues]  
 * Exemple : pas.evi.cumulus.od iOdImage pas.evi.cumulus.od iOdImage [36] setAssetName(java.lang.String) AS void [12323324] "ffff_gggg_dffer_dfr_2009_3.jpg"  
 * 
 * Methoc existing:
 * "END" [staticPackageName] [staticClassName] [packageName] [className] "[" [threadNumber] "]" [szMethodSignature] "AS" [returnedType] "[" [timeStmp] "]"
 * Exemple : END pas.evi.cumulus.od iOdImage pas.evi.cumulus.od iOdImage [36] setAssetName(java.lang.String) AS void [12323399]  
 */
void TraceLogger::Trace(char* szTraceType, char* szStaticPackageName, char* szStaticClassName, char* szPackageName, char* szClassName, char* szszMethodSignature, char* szszReturnType, char* szParameterValues)
{
	// Open trace file
    if (iFileSessionTracker > 0)
		fopen_s(&fStream, szTraceFilename, "a+");
    else 
		fopen_s(&fStream, szTraceFilename, "w");
    
	char* szInternalTraceType = (strcmp(szTraceType, "ENTER") == 0 ? "" : "END ");

	// Retrieve thread id
	long threadNumber = static_cast<long> (GetCurrentThreadId());

	// Retrieve current timestamp
    time_t timeValue = time(NULL);
	long timeStmp = static_cast<long> (timeValue);

	// Trace package
    fprintf(fStream, "%s%s %s %s %s [%u] %s AS %s [%u] %s\n", 
		szInternalTraceType,
		szStaticPackageName, 
		szStaticClassName, 
		szPackageName, 
		szClassName, 
		threadNumber,
		szszMethodSignature,
		szszReturnType,
		timeStmp,
		szParameterValues);

	iFileSessionTracker++;
    fclose(fStream);
}
