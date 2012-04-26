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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.imglib2.ops.Function;
import net.imglib2.ops.Tuple2;
import net.imglib2.ops.parse.token.CloseRange;
import net.imglib2.ops.parse.token.Comma;
import net.imglib2.ops.parse.token.OpenRange;
import net.imglib2.ops.parse.token.Token;
import net.imglib2.ops.parse.token.Variable;
import net.imglib2.type.numeric.real.DoubleType;

/*
 * Grammar
 * 
 * statement = axisNames , equation
 * 
 * axisNames = '[' axes ']'
 *
 * axes = 
 *   axisName |
 *   axisName , axes
 * 
 * equation = (see EquationParser)
 */

/**
 * Parses an equation string and attempts to build a
 * Function<long[],DoubleType> that matches the string
 * specification.
 * 
 * @author Barry DeZonia
 *
 */
public class RealEquationFunctionParser {

	private Map<String, Integer> varMap;
	private EquationParser eqnParser;
	
	public Tuple2<Function<long[],DoubleType>,String> parse(String specification) {
		varMap = new HashMap<String,Integer>();
		eqnParser = new EquationParser(varMap);
		Lexer lexer = new Lexer();
		ParseStatus lexResult = lexer.tokenize(specification, varMap);
		if (lexResult.errMsg != null) {
			return new Tuple2<Function<long[],DoubleType>,String>(lexResult.function,lexResult.errMsg);
		}
		ParseStatus parseResult = constructFunction(lexResult.tokens);
		return new Tuple2<Function<long[],DoubleType>,String>(parseResult.function, parseResult.errMsg);
	}
	
	private ParseStatus constructFunction(List<Token> tokens) {
		return statement(tokens);
	}

	/*
	 * statement = axisNames , equation
	 */
	private ParseStatus statement(List<Token> tokens) {
		ParseStatus status = axisNames(tokens, 0);
		if (status.errMsg != null) return status;
		if (ParseUtils.match(Comma.class, tokens, status.tokenNumber)) {
			return eqnParser.equation(tokens, status.tokenNumber+1);
		}
		else
			return ParseUtils.syntaxError(
					status.tokenNumber, tokens.get(status.tokenNumber),
					"Expected comma after axis designations");
	}
	
	/* 
	 * axisNames = '[' axes ']'
	 */
	private ParseStatus axisNames(List<Token> tokens, int pos) {
		if (!ParseUtils.match(OpenRange.class, tokens, pos))
			return ParseUtils.syntaxError(pos, tokens.get(pos),
					"Expected a '[' before axis name definitions");
		ParseStatus status = axes(tokens, pos+1);
		if (status.errMsg != null) return status;
		if (!ParseUtils.match(CloseRange.class, tokens, status.tokenNumber))
			return ParseUtils.syntaxError(
					status.tokenNumber, tokens.get(status.tokenNumber),
					"Expected a ']' after axis name definitions");
		return ParseUtils.nextPosition(status.tokenNumber+1);
	}
	
	/*
	 * axes = 
	 *   axisName |
	 *   axisName , axes
	 */
	private ParseStatus axes(List<Token> tokens, int pos) {
		if (!ParseUtils.match(Variable.class, tokens, pos))
			return ParseUtils.syntaxError(
					pos, tokens.get(pos), "Expected a name of an axis");
		Variable var = (Variable) tokens.get(pos);
		int dimIndex = varMap.get(var.getText());
		if (dimIndex >= 0)
			return ParseUtils.syntaxError(pos, tokens.get(pos),
				"Cannot declare axis name ("+var.getText()+") more than once");
		varMap.put(var.getText(), (-dimIndex)-1);  // mark bound
		if (ParseUtils.match(Comma.class, tokens, pos+1))
			return axes(tokens, pos+2);
		return ParseUtils.nextPosition(pos+1);
	}
	
	/*
	public static void main(String[] args) {
		RealEquationFunctionParser parser = new RealEquationFunctionParser();
		Tuple2<Function<long[],DoubleType>,String> results;
		
		results = parser.parse("[x,y],x^2+y^3");
		if (results.get2() != null)
			System.out.println(results.get2());
		else {
			DoubleType output = new DoubleType();
			results.get1().compute(new long[]{5,2}, output);
			if (output.getRealDouble() == 33.0)
				System.out.println("Success");
			else
				System.out.println("Failure: "+output.getRealDouble());
		}
	}
	*/
}
