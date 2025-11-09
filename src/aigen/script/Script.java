package aigen.script;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import aigen.description.AtomDescription;
import aigen.description.ChainDescription;
import aigen.description.Description;
import aigen.description.DescriptionGenerator;
import aigen.description.ResidueDescription;
import aigen.run.Pipe;
import aigen.run.Run;
import tailor.condition.AngleBoundCondition;
import tailor.condition.Condition;
import tailor.condition.DistanceBoundCondition;
import tailor.condition.HBondCondition;
import tailor.condition.PropertyCondition;
import tailor.condition.TorsionBoundCondition;
import tailor.measurement.AngleMeasure;
import tailor.measurement.DistanceMeasure;
import tailor.measurement.HBondMeasure;
import tailor.measurement.Measure;
import tailor.measurement.PropertyMeasure;
import tailor.measurement.TorsionMeasure;

/**
 * Main script parser and translator
 * 
 * First, create the ANTLR4 grammar file (MotifScript.g4):
 * 
 * grammar MotifScript;
 * 
 * script
 *     : motifDescription filepath filenames measures EOF
 *     ;
 * 
 * motifDescription
 *     : 'motifDescription' '=' predefinedMotif
 *     | 'motifDescription' '=' definedMotif
 *     ;
 * 
 * predefinedMotif
 *     : 'PREDEFINED' QUOTED_WORD
 *     ;
 * 
 * definedMotif
 *     : level+
 *     ;
 * 
 * level
 *     : levelName property+
 *     ;
 * 
 * levelName
 *     : 'Chain' | 'Residue' | 'Atom'
 *     ;
 * 
 * property
 *     : attributeName '=' attributeValue
 *     ;
 * 
 * attributeName
 *     : 'chainID' | 'chainType' | 'position' | 'resname'
 *     | 'hydrogen-bond' | 'torsion-bound' | 'distance-bound' | 'angle-bound'
 *     ;
 * 
 * attributeValue
 *     : selectionList? value+
 *     ;
 * 
 * selectionList
 *     : '(' selection+ ')'
 *     ;
 * 
 * selection
 *     : INTEGER '.' WORD
 *     ;
 * 
 * value
 *     : QUOTED_WORD | INTEGER | REAL | numberWithRange
 *     ;
 * 
 * numberWithRange
 *     : anyNum '+/-' anyNum
 *     ;
 * 
 * anyNum
 *     : REAL | INTEGER
 *     ;
 * 
 * filepath
 *     : 'filepath' '=' path
 *     ;
 * 
 * path
 *     : UNIX_PATH | WIN_PATH | '.'
 *     ;
 * 
 * filenames
 *     : 'filenames' '=' '[' WORD* ']'
 *     ;
 * 
 * measures
 *     : 'measures' '=' measure+
 *     ;
 * 
 * measure
 *     : geometricMeasure | propertyMeasure
 *     ;
 * 
 * geometricMeasure
 *     : geometricMeasureType selectionList
 *     ;
 * 
 * geometricMeasureType
 *     : 'Distance' | 'Angle' | 'Torsion' | 'HBond'
 *     ;
 * 
 * propertyMeasure
 *     : 'Property' INTEGER WORD valueType
 *     ;
 * 
 * valueType
 *     : 'int' | 'float' | 'str'
 *     ;
 * 
 * QUOTED_WORD : '\'' WORD '\'' ;
 * WORD : [a-zA-Z0-9]+ ;
 * INTEGER : '-'? [0-9]+ ;
 * REAL : INTEGER '.' [0-9]* ;
 * UNIX_PATH : ('~' | '.') ('/' WORD)+ '/'? ;
 * WIN_PATH : [a-zA-Z] ':\\\\' (WORD '\\')+ WORD? ;
 * COMMENT : '#' ~[\r\n]* -> skip ;
 * WS : [ \t\r\n]+ -> skip ;
 */
public class Script {
    
    // Type mappings
    private static final Map<String, Class<?>> TYPE_DICT = Map.of(
        "str", String.class,
        "int", Integer.class,
        "float", Double.class
    );
    
    // Level name to description class mapping
    private static final Map<String, Class<? extends Description>> LEVEL_NAME_DICT = Map.of(
        "Chain", ChainDescription.class,
        "Residue", ResidueDescription.class,
        "Atom", AtomDescription.class
    );
    
    // Description to sub-description mapping
    private static final Map<Class<? extends Description>, Class<? extends Description>> 
        DESC_TO_SUB_DESC_MAP = Map.of(
            ChainDescription.class, ResidueDescription.class,
            ResidueDescription.class, AtomDescription.class
        );
    
    // Keyword to condition mapping
    private static final Map<String, Class<? extends Condition>> KEYWORD_TO_CONDITION_MAP = Map.of(
        "chainID", PropertyCondition.class,
        "chainType", PropertyCondition.class,
        "position", PropertyCondition.class,
        "resname", PropertyCondition.class,
        "distance-bound", DistanceBoundCondition.class,
        "angle-bound", AngleBoundCondition.class,
        "torsion-bound", TorsionBoundCondition.class,
        "hydrogen-bond", HBondCondition.class
    );
    
    // Measure type mapping
    private static final Map<String, Class<? extends Measure>> MEASURE_DICT = Map.of(
        "Property", PropertyMeasure.class,
        "Distance", DistanceMeasure.class,
        "Angle", AngleMeasure.class,
        "Torsion", TorsionMeasure.class,
        "HBond", HBondMeasure.class
    );
    
    private static final Map<String, ChainDescription> PREDEFINED_DESCRIPTIONS = 
    		DescriptionGenerator.getPredefinedDescriptions();
    
    /**
     * Parse and execute a script file
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java Script <script-file>");
            System.exit(1);
        }
        
        try {
            String fileContent = new String(Files.readAllBytes(Paths.get(args[0])));
            System.out.println(fileContent);
            
            ParsedScript parsed = parseScript(fileContent);
            Run run = translate(parsed);
            run.run();
            
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Parse error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Parse script content (simplified parser without full ANTLR4)
     * In a real implementation, you would use ANTLR4-generated parser
     */
    public static ParsedScript parseScript(String content) {
        // This is a simplified manual parser
        // In production, you'd use ANTLR4 generated classes
        ParsedScript result = new ParsedScript();
        
        String[] lines = content.split("\n");
        StringBuilder currentSection = new StringBuilder();
        String currentKey = null;
        
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;
            
            if (line.startsWith("motifDescription")) {
                currentKey = "motifDescription =";
                String[] parts = line.split("=");
                result.motifDescription = parts[1].trim();
            } else if (line.startsWith("filepath")) {
                currentKey = "filepath";
                String[] parts = line.split("=");
                result.filepath = parts[1].trim();
            } else if (line.startsWith("filenames")) {
                currentKey = "filenames";
                String[] parts = line.split("=");
                String fileStr = parts[1].trim().replaceAll("[\\[\\]]", "");
                if (!fileStr.isEmpty()) {
                    result.filenames = Arrays.asList(fileStr.split("\\s+"));
                }
            } else if (line.startsWith("measures")) {
                currentKey = "measures";
                String[] parts = line.split("=");
                result.measures = parseMeasureText(parts[1]);
            } else if (currentKey != null) {
                currentSection.append(" ").append(line);
            }
        }
        
        return result;
    }
    
    private static List<MeasureText> parseMeasureText(String text) {
    	List<MeasureText> measureTexts = new ArrayList<>();
    	for (String textPart :  text.split(",")) {
    		int openBracket = textPart.indexOf("(");
    		String measureName = textPart.substring(0, openBracket).trim();
    		if (MEASURE_DICT.containsKey(measureName)) {
    			if (measureName.equals("Property")) {
    				int closedBracket = textPart.indexOf(")");
    				String[] subParts = textPart.substring(openBracket, closedBracket).split("\\s");
    				PropertyMeasureText p = new PropertyMeasureText();
    				p.measureType = "Property";
    				p.property = subParts[0];
    				p.residueNumber = Integer.valueOf(subParts[1]);
    				p.valueType = subParts[2];
    				measureTexts.add(p);
    			} else {
    				int closedBracket = textPart.indexOf(")");
    				String[] subParts = textPart.substring(openBracket + 1, closedBracket).split("\\s");
    				GeometricMeasureText g = new GeometricMeasureText();
    				g.measureType = measureName;
    				g.selections = parseSelections(subParts);
    				measureTexts.add(g);
    			}
    		} else {
    			// TODO throw error, potentially
    		}
    	}
    	return measureTexts;
    }
    
    private static List<Selection> parseSelections(String[] parts) {
    	List<Selection> selections = new ArrayList<>();
    	for (String part : parts) {
    		String[] bits = part.split("\\.");
    		selections.add(new Selection(Integer.parseInt(bits[0]), bits[1]));
    	}
    	return selections;
    }
    
    /**
     * Translate parsed script into a Run object
     */
    public static Run translate(ParsedScript parsed) {
        Description motifDescription;
        
        if (parsed.motifDescription != null && parsed.motifDescription.startsWith("PREDEFINED")) {
            motifDescription = lookupMotif(parsed.motifDescription);
        } else {
            motifDescription = makeMotif(parsed.motifDescriptionLevels);
        }
        
        List<String> filenames = parsed.filenames != null ? 
            parsed.filenames : new ArrayList<>();
        
        List<Measure> measureList = makeMeasures(motifDescription, parsed.measures);
        
        List<Pipe> pipes = List.of(new Pipe(motifDescription, measureList, System.out));
        
        return new Run(pipes, parsed.filepath, filenames);
    }
    
    /**
     * Look up a predefined motif description
     */
    public static ChainDescription lookupMotif(String motifDescriptionText) {
        String name = motifDescriptionText.split("'")[1];
        return PREDEFINED_DESCRIPTIONS.get(name);
    }
    
    /**
     * Create a motif description from parsed levels
     */
    public static Description makeMotif(List<LevelText> levelTexts) {
        Description motifDescription = null;
        Description currentLevel = null;
        Class<? extends Description> currentLevelSubType = null;
        
        // Create the Description hierarchy with PropertyConditions
        for (LevelText levelText : levelTexts) {
            try {
                // Create a Description object of the appropriate type
                Class<? extends Description> levelClass = LEVEL_NAME_DICT.get(levelText.levelName);
                Description level = levelClass.getDeclaredConstructor(Map.class)
                    .newInstance(new HashMap<>());
                
                // Add to motif description
                if (motifDescription == null) {
                    motifDescription = level;
                    currentLevel = motifDescription;
                    currentLevelSubType = DESC_TO_SUB_DESC_MAP.get(levelClass);
                } else {
                    if (level.getClass() == currentLevelSubType) {
                        currentLevel.addSubDescription(level);
                    }
                }
                
                // Add backbone atoms for Residue level
                if ("Residue".equals(levelText.levelName)) {
                    for (String name : Arrays.asList("N", "CA", "C", "O")) {
                        ((ResidueDescription) level).addAtom(name);
                    }
                }
                
                // Setup PropertyConditions
                for (PropertyText prop : levelText.properties) {
                    String key = prop.attributeName;
                    if (KEYWORD_TO_CONDITION_MAP.get(key) == PropertyCondition.class) {
                        level.addPropertyCondition(key, prop.values.get(0));
                    }
                }
                
            } catch (Exception e) {
                throw new RuntimeException("Error creating description: " + e.getMessage(), e);
            }
        }
        
        // Second pass: create Conditions that rely on the Description tree
        for (LevelText levelText : levelTexts) {
            for (PropertyText prop : levelText.properties) {
                String key = prop.attributeName;
                
                Class<? extends Condition> conditionClass = KEYWORD_TO_CONDITION_MAP.get(key);
                if (conditionClass != null && conditionClass != PropertyCondition.class) {
                    try {
                        List<Object> params = makeSelections(motifDescription, prop.selections);
                        params.addAll(prop.values.subList(prop.selections.size(), prop.values.size()));
                        
                        // Create condition instance
                        Condition condition = createCondition(conditionClass, params);
                        motifDescription.addCondition(condition);
                        
                    } catch (Exception e) {
                        throw new RuntimeException("Error creating condition: " + e.getMessage(), e);
                    }
                }
            }
        }
        
        motifDescription.setName("default");
        return motifDescription;
    }
    
    /**
     * Create selections from motif definition
     */
    public static List<Object> makeSelections(Description motifDefinition, 
                                             List<Selection> selectionTexts) {
        List<Object> selectionList = new ArrayList<>();
        
        // XXX TODO - how do we know this works?
        ChainDescription chainDescription = (ChainDescription) motifDefinition;
        
        for (Selection selection : selectionTexts) {
            int residueNumber = selection.residueNumber;
            String atomName = selection.atomName;
            
            ResidueDescription selectedResidue = chainDescription.selectResidue(residueNumber);
            Description selectedAtom = selectedResidue.selectAtom(atomName);
            
            selectionList.add(selectedAtom);
        }
        
        return selectionList;
    }
    
    /**
     * Create measures from parsed measure text
     */
    public static List<Measure> makeMeasures(Description motifDefinition, 
                                            List<MeasureText> measureTexts) {
        List<Measure> measures = new ArrayList<>();
        
        // XXX TODO - how do we know this works?
        ChainDescription chainDescription = (ChainDescription) motifDefinition;
        
        for (MeasureText measureText : measureTexts) {
            try {
                if ("Property".equals(measureText.measureType)) {
                    PropertyMeasureText pmt = (PropertyMeasureText) measureText;
                    Description selected = chainDescription.selectResidue(pmt.residueNumber);
                    Class valueType = TYPE_DICT.get(pmt.valueType);
                    
                    Measure measure = new PropertyMeasure(selected, pmt.property, valueType);
                    measures.add(measure);
                    
                } else {
                    GeometricMeasureText gmt = (GeometricMeasureText) measureText;
                    List<Object> selections = makeSelections(motifDefinition, gmt.selections);
                    
                    Class<? extends Measure> measureClass = MEASURE_DICT.get(gmt.measureType);
                    Measure measure = createMeasure(measureClass, selections);
                    measures.add(measure);
                }
            } catch (Exception e) {
                throw new RuntimeException("Error creating measure: " + e.getMessage(), e);
            }
        }
        
        return measures;
    }
    
    /**
     * Helper method to create condition instances
     */
    private static Condition createCondition(Class<? extends Condition> conditionClass, 
                                            List<Object> params) throws Exception {
        // Use reflection to find appropriate constructor
        Class<?>[] paramTypes = params.stream()
            .map(Object::getClass)
            .toArray(Class<?>[]::new);
        
        return conditionClass.getDeclaredConstructor(paramTypes)
            .newInstance(params.toArray());
    }
    
    /**
     * Helper method to create measure instances
     */
    private static Measure createMeasure(Class<? extends Measure> measureClass, 
                                        List<Object> params) throws Exception {
        Class<?>[] paramTypes = params.stream()
            .map(Object::getClass)
            .toArray(Class<?>[]::new);
        
        return measureClass.getDeclaredConstructor(paramTypes)
            .newInstance(params.toArray());
    }
}

/**
 * Data classes for parsed script
 */
class ParsedScript {
    public String motifDescription;
    public List<LevelText> motifDescriptionLevels = new ArrayList<>();
    public String filepath;
    public List<String> filenames = new ArrayList<>();
    public List<MeasureText> measures = new ArrayList<>();
}

class LevelText {
    public String levelName;
    public List<PropertyText> properties = new ArrayList<>();
}

class PropertyText {
    public String attributeName;
    public List<Selection> selections = new ArrayList<>();
    public List<Object> values = new ArrayList<>();
}

class Selection {
    public int residueNumber;
    public String atomName;
    
    public Selection(int residueNumber, String atomName) {
        this.residueNumber = residueNumber;
        this.atomName = atomName;
    }
}

abstract class MeasureText {
    public String measureType;
}

class GeometricMeasureText extends MeasureText {
    public List<Selection> selections = new ArrayList<>();
}

class PropertyMeasureText extends MeasureText {
    public int residueNumber;
    public String property;
    public String valueType;
}
