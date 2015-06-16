///////////////////////////////////////////////////////////////////////////////
// Copyright 2008-2015, Technische Universitaet Darmstadt (TUD), Germany
//
// The TUD licenses this file to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.
///////////////////////////////////////////////////////////////////////////////
package de.tud.stg.popart.pointcuts;

import java.util.regex.Pattern;

import de.tud.stg.popart.joinpoints.InitializationJoinPoint;
import de.tud.stg.popart.joinpoints.JoinPoint;
import de.tud.stg.popart.joinpoints.StaticJoinPoint;

public class InitializationPCD extends PrimitivePCD {
	
	private Pattern compiledSignatureRegExp;
	
	public InitializationPCD(String signatureRegExpr) {
		super("initialization("+signatureRegExpr+")");
		compiledSignatureRegExp = Pattern.compile(signatureRegExpr);
	}
	
	public boolean matchUncached(JoinPoint jp) {
		if (!(jp instanceof InitializationJoinPoint)) return false;
		Object targetObject = jp.context.get("targetObject");
		return (compiledSignatureRegExp.matcher(targetObject.getClass().getCanonicalName()).matches());
	}
	
	public Pointcut partialEvalUncached(StaticJoinPoint staticJoinPoint) {
		if (InitializationJoinPoint.class.isAssignableFrom(staticJoinPoint.getCorrespondingNonStaticJoinPointType())){
			return BooleanPCD.fromBoolean(compiledSignatureRegExp.matcher(staticJoinPoint.getReceiverClass().getCanonicalName()).matches());
		}else{
			return BooleanPCD.NEVER;
		}
	}
}