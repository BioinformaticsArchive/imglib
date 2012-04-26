/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */


package net.imglib2.ops.parse;

import java.util.List;
import java.util.Map;

import net.imglib2.ops.BinaryOperation;
import net.imglib2.ops.function.general.GeneralBinaryFunction;
import net.imglib2.ops.function.general.GeneralUnaryFunction;
import net.imglib2.ops.function.real.ConstantRealFunction;
import net.imglib2.ops.function.real.RealIndexFunction;
import net.imglib2.ops.operation.binary.real.RealAdd;
import net.imglib2.ops.operation.binary.real.RealDivide;
import net.imglib2.ops.operation.binary.real.RealMultiply;
import net.imglib2.ops.operation.binary.real.RealPower;
import net.imglib2.ops.operation.binary.real.RealSubtract;
import net.imglib2.ops.parse.token.CloseParen;
import net.imglib2.ops.parse.token.Divide;
import net.imglib2.ops.parse.token.Exponent;
import net.imglib2.ops.parse.token.FunctionCall;
import net.imglib2.ops.parse.token.Int;
import net.imglib2.ops.parse.token.Minus;
import net.imglib2.ops.parse.token.Mod;
import net.imglib2.ops.parse.token.OpenParen;
import net.imglib2.ops.parse.token.Plus;
import net.imglib2.ops.parse.token.Real;
import net.imglib2.ops.parse.token.Times;
import net.imglib2.ops.parse.token.Token;
import net.imglib2.ops.parse.token.Variable;
import net.imglib2.type.numeric.real.DoubleType;

/* Grammar

equation =
term |
term “+” term |
term “-” term

term =
factor |
factor “*” factor |
factor “\” factor |
factor “%” factor

factor =
signedAtom |
signedAtom “^” signedAtom

signedAtom
 atom |
 sign atom

atom =
identifier |
function “(“ equation “)” |
num |
“(“ equation “)”

function =
“log” | “exp” | “abs” | “ceil” | “floor” | “round” | “signum” | “sqrt” | “sqr” | ???

num = real | int | “E” | “PI”
(actually LEXER detects E and PI constants and creates appropriate Reals)

*/

/**
 * Used by other classes that need to parse equations. See
 * {@link PointSetParser} and {@link RealEquationFunctionParser} 
 * 
 * @author Barry DeZonia
 *
 */
public class EquationParser {
	
	private Map<String,Integer> varMap;
	
	public EquationParser(Map<String,Integer> varMap) {
		this.varMap = varMap;
	}
	
	/*
	equation =
	 term |
	 term “+” term |
	 term “-” term
	*/
	public ParseStatus equation(List<Token> tokens, int pos) {
		ParseStatus status1 = term(tokens, pos);
		ParseStatus status2 = status1;
		if (ParseUtils.match(Plus.class, tokens, status1.tokenNumber)) {
			status2 = term(tokens, status1.tokenNumber+1);
			status2.function = new
				GeneralBinaryFunction<long[],DoubleType,DoubleType,DoubleType>(
					status1.function, status2.function,
					new RealAdd<DoubleType,DoubleType,DoubleType>(),
					new DoubleType());
		}
		else if (ParseUtils.match(Minus.class, tokens, status1.tokenNumber)) {
			status2 = term(tokens, status1.tokenNumber+1);
			status2.function = new
				GeneralBinaryFunction<long[],DoubleType,DoubleType,DoubleType>(
					status1.function, status2.function,
					new RealSubtract<DoubleType,DoubleType,DoubleType>(),
					new DoubleType());
		}
		return status2;
	}
	
	/*
	term =
	 factor |
	 factor “*” factor |
	 factor “\” factor |
	 factor “%” factor
	*/
	private ParseStatus term(List<Token> tokens, int pos) {
		ParseStatus status1 = factor(tokens, pos);
		ParseStatus status2 = status1;
		if (ParseUtils.match(Times.class, tokens, status1.tokenNumber)) {
			status2 = factor(tokens, status1.tokenNumber+1);
			status2.function = new
				GeneralBinaryFunction<long[],DoubleType,DoubleType,DoubleType>(
					status1.function, status2.function,
					new RealMultiply<DoubleType,DoubleType,DoubleType>(),
					new DoubleType());
		}
		else if (ParseUtils.match(Divide.class, tokens, status1.tokenNumber)) {
			status2 = factor(tokens, status1.tokenNumber+1);
			status2.function = new
				GeneralBinaryFunction<long[],DoubleType,DoubleType,DoubleType>(
					status1.function, status2.function,
					new RealDivide<DoubleType,DoubleType,DoubleType>(),
					new DoubleType());
		}
		else if (ParseUtils.match(Mod.class, tokens, status1.tokenNumber)) {
			status2 = factor(tokens, status1.tokenNumber+1);
			status2.function = new
				GeneralBinaryFunction<long[],DoubleType,DoubleType,DoubleType>(
					status1.function, status2.function,
					new RealMod(), new DoubleType());
		}
		return status2;
	}
	
	/*
	factor =
	 signedAtom |
	 signedAtom “^” signedAtom
	*/
	private ParseStatus factor(List<Token> tokens, int pos) {
		ParseStatus status1 = signedAtom(tokens, pos);
		ParseStatus status2 = status1;
		if (ParseUtils.match(Exponent.class, tokens, status1.tokenNumber)) {
			status2 = signedAtom(tokens, status1.tokenNumber+1);
			status2.function = new
				GeneralBinaryFunction<long[],DoubleType,DoubleType,DoubleType>(
					status1.function, status2.function,
					new RealPower<DoubleType,DoubleType,DoubleType>(),
					new DoubleType());
		}
		return status2;
	}
	
	/*
	signedAtom
	  atom |
	  "+" atom |
	  "-" atom
	*/
	private ParseStatus signedAtom(List<Token> tokens, int pos) {
		if (ParseUtils.match(Plus.class, tokens, pos)) {
			return atom(tokens, pos+1);
		}
		else if (ParseUtils.match(Minus.class, tokens, pos)) {
			ParseStatus status = atom(tokens, pos+1);
			if (status.errMsg != null) return status;
			ConstantRealFunction<long[], DoubleType> constant =
				new ConstantRealFunction<long[], DoubleType>(new DoubleType(), -1);
			status.function = new
				GeneralBinaryFunction<long[],DoubleType,DoubleType,DoubleType>(
					constant, status.function,
					new RealMultiply<DoubleType,DoubleType,DoubleType>(),
					new DoubleType());
			return status;
		}
		else
			return atom(tokens, pos);
	}
	
	/*
	atom =
	 identifier |
	 function “(“ equation “)” |
	 num |
	 “(“ equation “)” 
	*/
	private ParseStatus atom(List<Token> tokens, int pos) {
		if (ParseUtils.match(Variable.class, tokens, pos)) {
			Variable var = (Variable) tokens.get(pos);
			int index = varMap.get(var.getText());
			if (index< 0)
				return ParseUtils.syntaxError(pos, tokens.get(pos),
						"Undeclared variable " + var.getText());
			ParseStatus status = new ParseStatus();
			status.tokenNumber = pos + 1;
			status.function = new RealIndexFunction(index);
			return status;
		}
		else if (ParseUtils.match(FunctionCall.class, tokens, pos)) {
			FunctionCall funcCall = (FunctionCall) tokens.get(pos);
			if (!ParseUtils.match(OpenParen.class, tokens, pos))
				ParseUtils.syntaxError(pos, tokens.get(pos),
							"Function call definition expected a '('");
			ParseStatus status = equation(tokens, pos+1);
			if (status.errMsg != null) return status;
			if (!ParseUtils.match(CloseParen.class, tokens, status.tokenNumber))
				return ParseUtils.syntaxError(
						status.tokenNumber,
						tokens.get(status.tokenNumber),
						"Function call definition expected a ')'");
			status.function =
				new GeneralUnaryFunction<long[], DoubleType, DoubleType>(
					status.function, funcCall.getOp(), new DoubleType());	
			status.tokenNumber++;
			return status;
		}
		else if (ParseUtils.match(OpenParen.class, tokens, pos)) {
			ParseStatus status = equation(tokens, pos+1);
			if (status.errMsg != null) return status;
			if (!ParseUtils.match(CloseParen.class, tokens, status.tokenNumber))
				return ParseUtils.syntaxError(status.tokenNumber, tokens.get(status.tokenNumber), "Expected a ')'");
			status.tokenNumber++;
			return status;
		}
		else
			return num(tokens, pos);
	}
	
	/*
	num = real | int
	*/
	private ParseStatus num(List<Token> tokens, int pos) {
		if (ParseUtils.match(Real.class, tokens, pos)) {
			Real r = (Real) tokens.get(pos);
			ParseStatus status = new ParseStatus();
			status.function = new ConstantRealFunction<long[],DoubleType>(new DoubleType(),r.getValue());
			status.tokenNumber = pos + 1;
			return status;
		}
		else if (ParseUtils.match(Int.class, tokens, pos)) {
			Int i = (Int) tokens.get(pos);
			ParseStatus status = new ParseStatus();
			status.function = new ConstantRealFunction<long[],DoubleType>(new DoubleType(),i.getValue());
			status.tokenNumber = pos + 1;
			return status;
		}
		else
			return ParseUtils.syntaxError(pos, tokens.get(pos), "Expected a number.");
	}
	
	// not a great function. will not make public.
	
	private class RealMod implements BinaryOperation<DoubleType, DoubleType, DoubleType> {

		@Override
		public DoubleType compute(DoubleType input1, DoubleType input2, DoubleType output) {
			long value = ((long) input1.get()) % ((long) input2.get());
			output.set(value);
			return output;
		}

		@Override
		public RealMod copy() {
			return new RealMod();
		}
		
	}
	

}
