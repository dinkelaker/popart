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

import de.tud.stg.popart.joinpoints.FieldAssignmentJoinPoint;
import de.tud.stg.popart.joinpoints.JoinPoint;
import de.tud.stg.popart.joinpoints.StaticJoinPoint;

public class FieldAssignmentPCD extends PrimitivePCD {
	
	private Pattern compiledSignatureRegExp;
	
	public FieldAssignmentPCD(String signatureRegExpr) {
		super("field_assignment("+signatureRegExpr+")");
		compiledSignatureRegExp = Pattern.compile(signatureRegExpr);
	}
	
	@Override
	public Pointcut partialEvalUncached(StaticJoinPoint staticJoinPoint) {
		if(FieldAssignmentPCD.class.isAssignableFrom(staticJoinPoint.getCorrespondingNonStaticJoinPointType())){
			return BooleanPCD.fromBoolean(compiledSignatureRegExp.matcher(staticJoinPoint.getMethodName()).matches());
		}else{
			return BooleanPCD.NEVER;
		}
	}
	
	public boolean matchUncached(JoinPoint jp) {
		if (jp instanceof FieldAssignmentJoinPoint){
			return compiledSignatureRegExp.matcher(((FieldAssignmentJoinPoint)jp).fieldName).matches();
		}else{
			return false;
		}
	}
}